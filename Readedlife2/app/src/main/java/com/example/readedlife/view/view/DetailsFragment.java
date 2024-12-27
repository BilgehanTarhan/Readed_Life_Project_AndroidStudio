package com.example.readedlife.view.view;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.room.Room;
import androidx.room.RoomSQLiteQuery;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.readedlife.R;
import com.example.readedlife.databinding.FragmentDetailsBinding;
import com.example.readedlife.view.Dao.BookDao;
import com.example.readedlife.view.Dao.BookDatabase;
import com.example.readedlife.view.Model.Book;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class DetailsFragment extends Fragment {
    String info = "";

    private FragmentDetailsBinding binding;
    //RegisterLauncher and permission stuff.
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;
    Bitmap selectedImage;
    //Database


    private final CompositeDisposable mDisposable = new CompositeDisposable();


    BookDao bookDao;
    BookDatabase bookDatabase;
    Book bookFromMain;
    SQLiteDatabase database;


    public DetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Binding --> layout
        binding = FragmentDetailsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerLauncher();
        // Database başlattığımız kısım.
        bookDatabase = Room.databaseBuilder(requireContext(), BookDatabase.class, "Book").build();
        bookDao = bookDatabase.bookDao();


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);


        database = requireActivity().openOrCreateDatabase("BookDatabase",Context.MODE_PRIVATE,null);

        if (getArguments() != null){
            info = DetailsFragmentArgs.fromBundle(getArguments()).getInfo();

        }else{
            info = "new";
        }
        binding.SaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save(v);
            }
        });

        binding.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(v);
            }
        });
        binding.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete(v);
            }
        });
    if (info.equals("new")){
    binding.bookNameText.setText("");
    binding.writerNameText.setText("");
    binding.readYear.setText("");
    binding.SaveButton.setVisibility(view.VISIBLE);
    binding.deleteButton.setVisibility(View.GONE);


    }else {
        int bookId =DetailsFragmentArgs.fromBundle(getArguments()).getBookId();
        binding.SaveButton.setVisibility(View.GONE);
        binding.deleteButton.setVisibility(View.VISIBLE);

        mDisposable.add(bookDao.getBookById(bookId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(DetailsFragment.this::handleResponseWithOldBook));

    }




    }
    private void handleResponseWithOldBook(Book book){
        bookFromMain = book;
        binding.bookNameText.setText(book.bookName);
        binding.readYear.setText(book.year);
        binding.writerNameText.setText(book.writerName);

        Bitmap bitmap = BitmapFactory.decodeByteArray(book.image,0,book.image.length);
        binding.imageView.setImageBitmap(bitmap);
    }

    public void selectImage(View view) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_MEDIA_IMAGES)) {
                    Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                        }
                    }).show();
                } else {
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                }
            } else {
                Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intentToGallery);
            }
        } else {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                        }
                    }).show();
                } else {
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                }
            } else {
                Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intentToGallery);
            }
        }

    }

    public void registerLauncher() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult o) {
                if (o.getResultCode() == Activity.RESULT_OK) {
                    Intent intentFromResult = o.getData();
                    if (intentFromResult != null) {
                        Uri imageData = intentFromResult.getData();
                        try {
                            if (Build.VERSION.SDK_INT >= 28) {
                                ImageDecoder.Source source = ImageDecoder.createSource(requireActivity().getContentResolver(), imageData);
                                selectedImage = ImageDecoder.decodeBitmap(source);
                                binding.imageView.setImageBitmap(selectedImage);
                            } else {
                                selectedImage = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageData);
                                binding.imageView.setImageBitmap(selectedImage);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }


            }
        });
        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean o) {
                if (o) {
                    //permission granted
                    Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentToGallery);
                } else {
                    //permission Denied
                    Toast.makeText(requireActivity(), "Permission Needed", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public Bitmap makeSmallerImage(Bitmap image, int maximumSize) {

        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;

        if (bitmapRatio > 1) {
            width = maximumSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maximumSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image, width, height, true);
    }
    public void save(View view){
        String bookName = binding.bookNameText.getText().toString();
        String writerName = binding.writerNameText.getText().toString();
        String readYear= binding.readYear.getText().toString();

        Bitmap smallImage = makeSmallerImage(selectedImage,300);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        smallImage.compress(Bitmap.CompressFormat.PNG,50,outputStream);
        byte[] byteArray = outputStream.toByteArray();

        Book book = new Book(bookName,writerName,readYear,byteArray);
        mDisposable.add(bookDao.insert(book).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(DetailsFragment.this::handleResponse));


    }public void handleResponse(){
        NavDirections action = DetailsFragmentDirections.actiondetailsfragmentToRecylerFragment();
        Navigation.findNavController(requireView()).navigate(action);
    }
    public void delete(View view){
        mDisposable.add(bookDao.delete(bookFromMain)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(DetailsFragment.this::handleResponse));
    }

}
