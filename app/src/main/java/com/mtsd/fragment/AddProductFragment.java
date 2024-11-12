package com.mtsd.fragment;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
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

import com.mtsd.util.DatabaseHelper;
import com.mtsd.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AddProductFragment extends Fragment {

    private EditText etProductName, etProductQuantity, etProductPrice, etProductDescription;
    private Button btnSaveProduct;
    private DatabaseHelper databaseHelper;
    private ImageView ivProductImage;
    private Uri imageReference;

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
                    }
                }
            });


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
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_add_product, container, false);

        // Initialize views
        etProductName = rootView.findViewById(R.id.etProductName);
        etProductQuantity = rootView.findViewById(R.id.etProductQuantity);
        etProductPrice = rootView.findViewById(R.id.etProductPrice);
        etProductDescription = rootView.findViewById(R.id.etProductDescription);
        btnSaveProduct = rootView.findViewById(R.id.btnSaveProduct);
        ivProductImage = rootView.findViewById(R.id.ivProductImage);

        // Initialize DatabaseHelper
        databaseHelper = new DatabaseHelper(getContext());

        // Set up the save button click listener
        btnSaveProduct.setOnClickListener(v -> saveProduct());

        // Set up the image select button listener
        rootView.findViewById(R.id.btnSelectImage).setOnClickListener(v -> openImagePicker());

        return rootView;
    }

    private void saveProduct() {
        // Get input data from EditTexts
        String name = etProductName.getText().toString().trim();
        String quantityStr = etProductQuantity.getText().toString().trim();
        String priceStr = etProductPrice.getText().toString().trim();
        String description = etProductDescription.getText().toString().trim();

        // Check for empty fields
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(quantityStr) || TextUtils.isEmpty(priceStr) || TextUtils.isEmpty(description)) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert quantity and price to proper data types
        int quantity = Integer.parseInt(quantityStr);
        double price = Double.parseDouble(priceStr);

        // Copy image to custom folder and get new file path
        String imagePath = "";
        if (imageReference != null) {
            imagePath = saveImageToCustomFolder(imageReference);
        }

        // Insert the product into the database, include image path if available
        boolean isInserted = databaseHelper.insertProduct(name, quantity, price, description, imagePath);

        // Show success or failure message
        if (isInserted) {
            Toast.makeText(getContext(), "Product added successfully!", Toast.LENGTH_SHORT).show();
            clearFields();
        } else {
            Toast.makeText(getContext(), "Failed to add product", Toast.LENGTH_SHORT).show();
        }
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

}
