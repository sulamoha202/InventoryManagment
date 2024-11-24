package com.mtsd.fragment;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.mtsd.OnProductUpdatedListener;
import com.mtsd.util.DatabaseHelper;
import com.mtsd.R;
import com.mtsd.model.Product;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class EditProductFragment extends Fragment {

    private EditText etName, etQuantity, etPrice, etDescription;
    private ImageView ivProductImage;
    private Button btnSave;
    private DatabaseHelper dbHelper;
    private int productId;
    private Uri imageReference;

    public EditProductFragment() { }
    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    if (result.getData().getData() != null) {
                        // Handle gallery image
                        Uri selectedImageUri = result.getData().getData();
                        String savedImagePath = saveImageToCustomFolder(selectedImageUri);
                        if (!savedImagePath.isEmpty()) {
                            imageReference = Uri.fromFile(new File(savedImagePath));
                            ivProductImage.setImageURI(imageReference);
                            Log.d("IMAGE LOG", "Image saved at: " + savedImagePath);
                        }
                    } else if (result.getData().getExtras() != null) {
                        // Handle camera image
                        Bitmap photo = (Bitmap) result.getData().getExtras().get("data");
                        imageReference = saveBitmapToFile(photo);
                        ivProductImage.setImageBitmap(photo);
                        Log.d("IMAGE LOG", "Captured photo saved at: " + imageReference);
                    }
                }
            });


    private Uri saveBitmapToFile(Bitmap bitmap) {
        // Save the Bitmap to the internal storage (app-specific folder)
        File directory = new File(getContext().getFilesDir(), "product_images");
        if (!directory.exists()) {
            directory.mkdirs(); // Create the directory if it doesn't exist
        }

        String fileName = "product_" + System.currentTimeMillis() + ".jpg";
        File file = new File(directory, fileName);

        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out); // Save as JPEG
            out.flush();
            return Uri.fromFile(file); // Return the file Uri
        } catch (IOException e) {
            e.printStackTrace();
            return null; // Return null if saving fails
        }
    }


    private void openImagePicker() {
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickPhotoIntent.setType("image/*");

        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        Intent chooserIntent = Intent.createChooser(pickPhotoIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{takePhotoIntent});

        pickImage.launch(chooserIntent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_product, container, false);

        etName = view.findViewById(R.id.etProductName);
        etQuantity = view.findViewById(R.id.etProductQuantity);
        etPrice = view.findViewById(R.id.etProductPrice);
        etDescription = view.findViewById(R.id.etProductDescription);
        ivProductImage = view.findViewById(R.id.ivProductImage);
        btnSave = view.findViewById(R.id.btnSaveProduct);

        dbHelper = new DatabaseHelper(getContext());
        productId = getArguments().getInt("productId", -1);
        loadProductDetails(productId);

        view.findViewById(R.id.btnSelectImage).setOnClickListener(v -> openImagePicker());
        btnSave.setOnClickListener(v -> updateProduct());

        return view;
    }

    private void loadProductDetails(int id) {
        Product product = dbHelper.getProductById(id);
        if (product != null) {
            etName.setText(product.getName());
            etQuantity.setText(String.valueOf(product.getQuantity()));
            etPrice.setText(String.valueOf(product.getPrice()));
            etDescription.setText(product.getDescription());
            imageReference = Uri.parse(product.getImageReference());
            ivProductImage.setImageURI(imageReference);
        }
    }
    private void updateProduct() {
        String name = etName.getText().toString();
        int quantity = Integer.parseInt(etQuantity.getText().toString());
        double price = Double.parseDouble(etPrice.getText().toString());
        String description = etDescription.getText().toString();
        String imagePath = null;

        if (imageReference != null && imageReference.getScheme() != null) {
            if (imageReference.getScheme().equals("content")) {
                imagePath = saveImageToCustomFolder(imageReference); // Save content URI to internal storage
            } else if (imageReference.getScheme().equals("file")) {
                imagePath = imageReference.getPath(); // Use file path directly
            }
            Log.d("EditProductFragment", "Saving new image at: " + imagePath);
        } else {
            Product product = dbHelper.getProductById(productId);
            if (product != null) {
                imagePath = product.getImageReference();
                Log.d("EditProductFragment", "Retaining old image at: " + imagePath);
            }
        }


        Product product = new Product(productId, name, quantity, price, description, imagePath);
        boolean isUpdated = dbHelper.updateProduct(product);

        if (isUpdated) {
            Toast.makeText(getContext(), "Product updated successfully!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Failed to update product!", Toast.LENGTH_SHORT).show();
        }

        if (getActivity() instanceof OnProductUpdatedListener) {
            ((OnProductUpdatedListener) getActivity()).onProductUpdated();
        }

        getActivity().getSupportFragmentManager().popBackStack();
    }



    private String saveImageToCustomFolder(Uri imageUri) {
        File folder = new File(getContext().getFilesDir(), "product_images");
        if (!folder.exists()) {
            folder.mkdir();
        }

        String imageFileName = "product_" + System.currentTimeMillis() + ".jpg";
        File destinationFile = new File(folder, imageFileName);

        try {
            // If the image is a URI (from gallery), we can copy it directly
            if (imageUri != null) {
                InputStream inputStream = getContext().getContentResolver().openInputStream(imageUri);
                FileOutputStream outputStream = new FileOutputStream(destinationFile);

                // Copy the image to custom folder
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
                inputStream.close();
                outputStream.flush();
                outputStream.close();

                return destinationFile.getAbsolutePath();
            }

        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        return "";
    }


    private Bitmap resizeImage(Uri imageUri) throws IOException {
        // Load the image as a Bitmap
        ContentResolver contentResolver = getContext().getContentResolver();
        InputStream inputStream = contentResolver.openInputStream(imageUri);
        Bitmap originalBitmap = BitmapFactory.decodeStream(inputStream);
        inputStream.close();

        // Resize the Bitmap to 600x600
        return Bitmap.createScaledBitmap(originalBitmap, 600, 600, false);
    }
}
