package com.bignerdranch.android.budgetapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ItemFragment extends Fragment {
    private static final String ARG_ITEM_ID = "item_id";
    private static final String DIALOG_DATE = "DialogDate";

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_PHOTO = 1;

    private Item mItem;
    private File mPhotoFile;
    private EditText mTitleField;
    private Button mDateButton;
    private EditText mValueField;
    private EditText mQuantityField;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private Callbacks mCallbacks;

    public interface Callbacks {
        void onItemUpdated(Item item);
    }

    public static ItemFragment newInstance(UUID itemId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_ITEM_ID, itemId);

        ItemFragment fragment = new ItemFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID itemId = (UUID) getArguments().getSerializable(ARG_ITEM_ID);
        mItem = Budget.get(getActivity()).getItem(itemId);
        mPhotoFile = Budget.get(getActivity()).getPhotoFile(mItem);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_item, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Item");

        mTitleField = (EditText) v.findViewById(R.id.item_title);
        mTitleField.setText(mItem.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                mItem.setTitle(s.toString());
                updateItem();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //
            }
        });

        mValueField = (EditText) v.findViewById(R.id.item_value);
        mValueField.setText(Integer.toString(mItem.getValue()));
        mValueField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                try {
                    int value = Integer.parseInt(s.toString());
                    mItem.setValue(value);
                    updateItem();
                } catch(Exception e) {
                    Toast.makeText(getActivity(), "Can't leave this field blank!", Toast.LENGTH_LONG).show();
                    mItem.setValue(0);
                    updateItem();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //
            }
        });

        mQuantityField = (EditText) v.findViewById(R.id.item_quantity);
        mQuantityField.setText(mItem.getTitle());
        mQuantityField.setText(Integer.toString(mItem.getQuantity()));
        mQuantityField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                try {
                    int quantity = Integer.parseInt(s.toString());
                    mItem.setQuantity(quantity);
                    updateItem();
                } catch(Exception e) {
                    Toast.makeText(getActivity(), "Can't leave this field blank!", Toast.LENGTH_LONG).show();
                    mItem.setQuantity(0);
                    updateItem();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //
            }
        });

        mDateButton = v.findViewById(R.id.item_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment
                        .newInstance(mItem.getDate());
                dialog.setTargetFragment(ItemFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        PackageManager packageManager = getActivity().getPackageManager();
        mPhotoButton = (ImageButton) v.findViewById(R.id.item_camera);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        boolean canTakePhoto = mPhotoFile != null &&
                captureImage.resolveActivity(packageManager) != null;
        mPhotoButton.setEnabled(canTakePhoto);

        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Uri uri = FileProvider.getUriForFile(getActivity(),
                        "com.bignerdranch.android.budgetapplication.fileprovider",
                        mPhotoFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                List<ResolveInfo> cameraActivities = getActivity()
                        .getPackageManager().queryIntentActivities(captureImage,
                                PackageManager.MATCH_DEFAULT_ONLY);

                for(ResolveInfo activity : cameraActivities) {
                    getActivity().grantUriPermission(activity.activityInfo.packageName,
                            uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }

                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });

        mPhotoView = (ImageView) v.findViewById(R.id.item_photo);
        updatePhotoView();

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode != Activity.RESULT_OK){
            return;
        }

        if(requestCode == REQUEST_DATE){
            Date date = (Date) data
                    .getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mItem.setDate(date);
            updateItem();
            updateDate();
        } else if (requestCode == REQUEST_PHOTO) {
            Uri uri = FileProvider.getUriForFile(getActivity(),
                    "com.bignerdranch.android.budgetapplication.fileprovider",
                    mPhotoFile);

            getActivity().revokeUriPermission(uri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            updateItem();
            updatePhotoView();
        }
    }

    private void updateItem() {
        Budget.get(getActivity()).updateItem(mItem);
        mCallbacks.onItemUpdated(mItem);
    }

    private void updateDate() {
        mDateButton.setText(mItem.getDate().toString());
    }

    private void updatePhotoView() {
        if(mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(
                    mPhotoFile.getPath(), getActivity()
            );
            mPhotoView.setImageBitmap(bitmap);
        }
    }
}
