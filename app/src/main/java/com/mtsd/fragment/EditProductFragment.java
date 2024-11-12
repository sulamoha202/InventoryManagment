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
                    // Check if the result is from the gallery (URI)
                    if (result.getData().getData() != null) {
                        imageReference = result.getData().getData();
                        ivProductImage.setImageURI(imageReference);
                        Log.d("IMAGE LOG", "Image URI: " + imageReference.getPath());
                    }

                    // Check if the result is from the camera (Bitmap)
                    else if (result.getData().getExtras() != null) {
                        Bitmap photo = (Bitmap) result.getData().getExtras().get("data");
                        ivProductImage.setImageBitmap(photo);
                        Log.d("IMAGE LOG", "Captured photo: " + photo);
                    }
                }
            });

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


        String imagePath = "";
        if (imageReference != null) {
            imagePath = saveImageToCustomFolder(imageReference);
            Log.d("EditProductFragment", "Saving image at: " + imagePath);
        }

        Product product = new Product(productId, name, quantity, price, description, imagePath);
        boolean isUpdated = dbHelper.updateProduct(product);

        if (isUpdated) {
            Toast.makeText(getContext(), "Product updated successfully!", Toast.LENGTH_SHORT).show();
            Log.d("EditProductFragment", "Product updated successfully!");

        } else {
            Toast.makeText(getContext(), "Failed to update product!", Toast.LENGTH_SHORT).show();
            Log.d("EditProductFragment", "Failed to update product!");

        }
        // Notify the parent fragment (ProductListFragment) that the product is updated
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
            // Resize the image to 600x600 before saving
            Bitmap bitmap = resizeImage(imageUri);

            // Save the resized image to a file
            FileOutputStream outputStream = new FileOutputStream(destinationFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);  // Compress the image
            outputStream.flush();
            outputStream.close();

            return destinationFile.getAbsolutePath();

        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
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
