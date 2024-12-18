package com.mtsd.fragment;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
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

import com.mtsd.helper.RepositoryManager;
import com.mtsd.helper.DatabaseHelper;
import com.mtsd.R;
import com.mtsd.model.Product;
import com.mtsd.repository.ProductRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class AddProductFragment extends Fragment {

    private EditText etProductName, etProductQuantity, etProductPrice, etProductDescription;
    private Button btnSaveProduct;
    private ImageView ivProductImage;
    private Uri imageReference;

    private RepositoryManager repositoryManager;


    private static final int PICK_IMAGE_REQUEST = 1;

    public AddProductFragment() {
        // Required empty public constructor
    }

    // Use ActivityResultContracts for image picking
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

                        // Save the image captured from the camera
                        imageReference = saveBitmapToCustomFolder(photo); // Save the Bitmap and return its path
                    }
                }
            });

    // Method to save Bitmap from Camera to a custom folder
    private Uri saveBitmapToCustomFolder(Bitmap photo) {
        File folder = new File(getContext().getFilesDir(), "product_images");
        if (!folder.exists()) {
            folder.mkdir();
        }

        String imageFileName = "product_" + System.currentTimeMillis() + ".jpg";
        File destinationFile = new File(folder, imageFileName);

        try {
            // Save the Bitmap to a file
            FileOutputStream outputStream = new FileOutputStream(destinationFile);
            photo.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);  // Compress the image
            outputStream.flush();
            outputStream.close();

            // Return the URI of the saved image
            return Uri.fromFile(destinationFile);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    private void openImagePicker() {
        // Create an Intent to pick an image from the gallery
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickPhotoIntent.setType("image/*");

        // Create an Intent to capture a photo using the camera
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Combine the intents into one chooser
        Intent chooserIntent = Intent.createChooser(pickPhotoIntent, "Select Image");

        // Add camera option to the chooser
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{takePhotoIntent});

        // Start the chooser activity
        pickImage.launch(chooserIntent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_product, container, false);

        etProductName = rootView.findViewById(R.id.etProductName);
        etProductQuantity = rootView.findViewById(R.id.etProductQuantity);
        etProductPrice = rootView.findViewById(R.id.etProductPrice);
        etProductDescription = rootView.findViewById(R.id.etProductDescription);
        btnSaveProduct = rootView.findViewById(R.id.btnSaveProduct);
        ivProductImage = rootView.findViewById(R.id.ivProductImage);

        SQLiteDatabase database = new DatabaseHelper(requireContext()).getWritableDatabaseInstance();
        repositoryManager = new RepositoryManager(database);

        btnSaveProduct.setOnClickListener(v -> saveProduct());

        rootView.findViewById(R.id.btnSelectImage).setOnClickListener(v -> openImagePicker());

        return rootView;
    }

    private void saveProduct() {
        ProductRepository productRepository = repositoryManager.getProductRepository();

        String name = etProductName.getText().toString().trim();
        String quantityStr = etProductQuantity.getText().toString().trim();
        String priceStr = etProductPrice.getText().toString().trim();
        String description = etProductDescription.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(quantityStr) || TextUtils.isEmpty(priceStr) || TextUtils.isEmpty(description)) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity = Integer.parseInt(quantityStr);
        double price = Double.parseDouble(priceStr);

        String imagePath = "";
        if (imageReference != null) {
            imagePath = saveImageToCustomFolder(imageReference);
        }
        Product newProduct = new Product(0,name,quantity,price,description,imagePath);

        // Insert the product into the database, include image path if available
        boolean isInserted =  productRepository.insert(newProduct) ;//databaseHelper.insertProduct(name, quantity, price, description, imagePath);

        // Show success or failure message
        if (isInserted) {
            Toast.makeText(getContext(), "Product added successfully!", Toast.LENGTH_SHORT).show();
            clearFields();
        } else {
            Toast.makeText(getContext(), "Failed to add product", Toast.LENGTH_SHORT).show();
        }

        returnToPrevouisFragment(new ProductListFragment());
    }

    private void clearFields() {
        // Clear all fields after successful insertion
        etProductName.setText("");
        etProductQuantity.setText("");
        etProductPrice.setText("");
        etProductDescription.setText("");
        ivProductImage.setImageResource(R.drawable.baseline_image_24); // Reset image
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

    private void returnToPrevouisFragment(Fragment fragment){
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contentFrame, fragment) // `contentFrame` should be the container for fragments
                .addToBackStack(null) // Optional: Adds the transaction to the back stack
                .commit();
    }

}
