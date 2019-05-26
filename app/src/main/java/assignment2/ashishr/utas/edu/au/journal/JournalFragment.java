package assignment2.ashishr.utas.edu.au.journal;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.RenderProcessGoneDetail;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.app.Activity.RESULT_OK;


public class JournalFragment extends Fragment {
    enum Mode
    {
        Adding, Editing, Deleting, Viewing
    }
    private ListView myList;
    private FragmentManager fm;
    private JournalAdapter entryListAdapter;
    ArrayList<Entry> entries = new ArrayList<Entry>();
    Database databaseConnection;
    SQLiteDatabase db;
    private int mode = 1;
    //Mode mMode = Mode.Viewing;
    private int selectedEntry = -1;
    private Spinner moodSpinner;
    private MoodAdapter moodAdapter;
    private String datestr;
    private int[] moodResources  = {R.drawable.depressed_face, R.drawable.sad_face, R.drawable.neutral_face, R.drawable.smile_face, R.drawable.happy_face};
    public static final int IMAGE_GALLERY_REQUEST = 20;//from gallery
    static final int REQUEST_IMAGE_CAPTURE = 1;//from camera
    private ImageView imageView;
    //permission
    static final int READ_REQUEST_CODE = 1;
    private String imageURI = "";

    public JournalFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View inflatedView = inflater.inflate(R.layout.fragment_journal, container, false);

        databaseConnection = new Database(getActivity());
        db = databaseConnection.open();

        //++ruihao   make sure the database can select journal by date when get open
        final Calendar calendar = Calendar.getInstance();
        final int currentDate = calendar.get(Calendar.DAY_OF_MONTH);
        final int currentMonth = calendar.get(Calendar.MONTH);
        final int currentYear = calendar.get(Calendar.YEAR);
        final TextView date = inflatedView.findViewById(R.id.dateText);//++++ruihao

        datestr = currentYear+"/"+String.format("%02d",currentMonth+1)+"/"+String.format("%02d",currentDate);
        date.setText(datestr);//month start from 0 to 11,so month should+1
        entries = JournalTable.selectByDate(db,date.getText().toString());//++++ruihao

        //show all the data
        //entries = JournalTable.selectAll(db);
        for (int i = 0; i < entries.size(); i++) {
            Entry j = entries.get(i);
            //Log.d("FOUND", j.getmEntryID() + ": " + j.getmEntryTitle());
        }

        myList = inflatedView.findViewById(R.id.myList);
        entryListAdapter = new JournalAdapter(getActivity().getApplicationContext(), R.layout.custom_list_layout, entries);
        myList.setAdapter(entryListAdapter);

        moodSpinner = (Spinner) inflatedView.findViewById(R.id.spinnerMood);
        moodAdapter = new MoodAdapter(getContext(),
                new Integer[]{R.drawable.depressed_face, R.drawable.sad_face, R.drawable.neutral_face,
                        R.drawable.smile_face, R.drawable.happy_face});
        moodSpinner.setAdapter(moodAdapter);
        moodSpinner.setSelection(2);

        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Entry p = entries.get(i);
                //Log.d("debugger","URI is" + p.getmEntryImage());
                SelectEntry(p.getmEntryID());
            }
        });

        myList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final Entry p = entries.get(position);
                if(mode == 1) {
                    //DeleteEntry(p.getmEntryID());
                }
                return false;
            }
        });

        Button deleteButton = inflatedView.findViewById(R.id.btnDelete);
        deleteButton.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View v) {
                //final Entry p = entries.get(selectedEntry);
                //Entry entry = JournalTable.selectByID(db,selectedEntry);

                if(selectedEntry != -1) {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    //Yes button clicked
                                    DeleteEntry(selectedEntry);
                                    EditText t1 = (EditText) inflatedView.findViewById(R.id.inputText);
                                    EditText t2 = (EditText) inflatedView.findViewById(R.id.inputTitle);
                                    t1.setText("");
                                    t2.setText("");
                                    imageURI = "";
                                    DisplayFragment(false);
                                    hideKeyboard(getActivity());
                                    UpdateList(datestr);
                                    selectedEntry = -1;

                                    //Log.d("FOUND","DELETED");
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked
                                    //Log.d("FOUND","NO");

                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Delete").setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();
                }
                //Log.d("FOUND", "entry: " + entry.getmEntryTitle());

            }
        });

        Button calenderButton = inflatedView.findViewById(R.id.datePick);
        calenderButton.setOnClickListener(new AdapterView.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog=new DatePickerDialog(
                        getActivity(), new DatePickerDialog.OnDateSetListener()
                        {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
                            {
                                datestr = year+"/"+String.format("%02d",(month+1))+"/"+String.format("%02d",dayOfMonth);
                                date.setText(datestr);//month start from 0 to 11,so month should+1

                                /*
                                entries = JournalTable.selectByDate(db,datestr);
                                entryListAdapter = new JournalAdapter(getActivity().getApplicationContext(), R.layout.custom_list_layout, entries);
                                myList.setAdapter(entryListAdapter);
                                */
                                UpdateList(datestr);
                            }
                        },currentYear,currentMonth,currentDate);

                datePickerDialog.show();


            }

        });

        /*
        long myDate = System.currentTimeMillis();

        SimpleDateFormat format = new SimpleDateFormat("dd MMM");
        String dateString = format.format(myDate);


        TextView date = inflatedView.findViewById(R.id.dateText);
        date.setText(dateString);


        Button calenderButton = inflatedView.findViewById(R.id.datePick);
        CalendarView myCalendar = inflatedView.findViewById(R.id.calendarView);

        calenderButton.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dFragment = new DatePickerFragment();

                dFragment.show(getFragmentManager(), "Date Picker");

            }
        });
        */

        fm = getChildFragmentManager();
        DisplayFragment(false);


        Button cancelBtn = inflatedView.findViewById(R.id.btnClose);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked

                                //Log.d("frags","cancel");
                                EditText t1 = (EditText) getActivity().findViewById(R.id.inputText);
                                EditText t2 = (EditText) getActivity().findViewById(R.id.inputTitle);
                                t1.setText("");
                                t2.setText("");
                                imageURI = "";
                                DisplayFragment(false);
                                hideKeyboard(getActivity());
                                selectedEntry = -1;
                                if(entries.isEmpty()){
                                    inflatedView.findViewById(R.id.helpEntries).setVisibility(View.VISIBLE);
                                    //Log.d("FOUND","EMPTY");
                                }else{
                                    inflatedView.findViewById(R.id.helpEntries).setVisibility(View.GONE);
                                }
                                //Log.d("frags","yes");
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                //Log.d("frags","no");
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Discard changes?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) inflatedView.findViewById(R.id.btnAddEntry);
        fab.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View view) {
                inflatedView.findViewById(R.id.helpEntries).setVisibility(View.GONE);
                Camera();
                imageView.setImageBitmap(null);
                if (fm == null) {
                    //Log.d("FOUND", "NULL");
                } else {

                    if (fm.findFragmentById(R.id.entryFragment).isVisible()) {
                        if(mode == 1) {
                            AddEntry();
                            selectedEntry = -1;
                            imageURI = "";
                            //Log.d("FOUND","ADDED");
                        }
                        else if(mode == 2){
                            UpdateEntry(selectedEntry);
                            mode = 1;
                            selectedEntry = -1;
                            imageURI = "";
                            //Log.d("FOUND","EDIT");
                        }
                        //entryListAdapter.notifyDataSetChanged();

                        DisplayFragment(false);

                        final InputMethodManager imm = (InputMethodManager) getActivity()
                                .getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

                        //Snackbar.make(view, "Entry Added", Snackbar.LENGTH_LONG).
                                //setAction("Action", null).show();

                        EditText t1 = (EditText) inflatedView.findViewById(R.id.inputText);
                        EditText t2 = (EditText) inflatedView.findViewById(R.id.inputTitle);
                        t1.setText("");
                        t2.setText("");
                        imageURI = "";

                    } else {
                        mode = 1;
                        moodSpinner.setSelection(2);
                        EditText t1 = (EditText) inflatedView.findViewById(R.id.inputText);
                        EditText t2 = (EditText) inflatedView.findViewById(R.id.inputTitle);
                        t2.requestFocus();

                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(t2, InputMethodManager.SHOW_IMPLICIT);

                        long myTime = System.currentTimeMillis();

                        SimpleDateFormat format = new SimpleDateFormat("h:mm a");
                        String dateString = format.format(myTime);
                        TextView timeText = inflatedView.findViewById(R.id.inputTime);
                        timeText.setText(dateString);

                        DisplayFragment(true);
                        Button btnDelete = inflatedView.findViewById(R.id.btnDelete);
                        Button btnShare = inflatedView.findViewById(R.id.btnShare);
                        imageView.setVisibility(View.GONE);
                        btnDelete.setVisibility(View.GONE);
                        btnShare.setVisibility(View.GONE);
                        //Log.d("FOUND", "YAY");

                        moodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView adapterView, View view, int i, long l) {
                                //Toast.makeText(getActivity().getApplicationContext(),""+ a[i], Toast.LENGTH_SHORT).show();
                                //Log.d("FOUND",""+moodAdapter.getMood(i));
                            }

                            @Override
                            public void onNothingSelected(AdapterView adapterView) {

                            }
                        });
                    }
                }
            }
        });

        if(entries.isEmpty()){
            inflatedView.findViewById(R.id.helpEntries).setVisibility(View.VISIBLE);
            //Log.d("FOUND","EMPTY");
        }else{
            inflatedView.findViewById(R.id.helpEntries).setVisibility(View.GONE);
        }
        return inflatedView;
    }

    public void AddEntry(){
        EditText title = getActivity().findViewById(R.id.inputTitle);
        EditText text = getActivity().findViewById(R.id.inputText);
        TextView date = getActivity().findViewById(R.id.dateText);//ruihao
        TextView timeText = getActivity().findViewById(R.id.inputTime);

        //Log.d("FOUND",timeText.getText().toString());

        Entry entry = new Entry();
        entry.setmEntryTitle(title.getText().toString());
        entry.setmEntryText(text.getText().toString());
        entry.setmEntryDate(date.getText().toString());
        entry.setmEntryImage(imageURI);
        Log.d("debugger","addingURI " + imageURI);

        ImageView image = getActivity().findViewById(R.id.inputImage);
        image.setVisibility(View.GONE);
        image.setImageURI(null);
        image.setImageBitmap(null);

        //Log.d("entrydate",entry.getmEntryDate());

        entry.setmEntryTime(timeText.getText().toString());
        entry.setmEntryMood(moodSpinner.getSelectedItemPosition());

        //Log.d("FOUND",""+entry.getmEntryMood());

        ImageView mImage = getActivity().findViewById(R.id.moodImage);
        mImage.setImageResource(moodResources[entry.getmEntryMood()]);


        JournalTable.insert(db,entry);
        //entries = JournalTable.selectAll(db);
        entries = JournalTable.selectByDate(db,entry.getmEntryDate());
        entryListAdapter = new JournalAdapter(getActivity().getApplicationContext(), R.layout.custom_list_layout, entries);
        myList.setAdapter(entryListAdapter);
    }


    public void SelectEntry(int id){
        final Entry entry = JournalTable.selectByID(db,id);
        EditText title = getActivity().findViewById(R.id.inputTitle);
        EditText text = getActivity().findViewById(R.id.inputText);
        TextView time = getActivity().findViewById(R.id.inputTime);
        title.setText(entry.getmEntryTitle());
        text.setText(entry.getmEntryText());
        time.setText(entry.getmEntryTime());
        moodSpinner.setSelection(entry.getmEntryMood());
        imageURI = entry.getmEntryImage();
        if(!imageURI.equals("")){
            /*
            ImageView image = getActivity().findViewById(R.id.inputImage);
            setPic(image, imageURI);
            image.setVisibility(View.VISIBLE);
            Log.d("debugger","URI is " + imageURI);
            */
            //InputStream inputStream = getActivity().getContentResolver().openInputStream(imageUri);
                //get a bitmap from a stream
            //Bitmap image= BitmapFactory.decodeStream(inputStream);
            //imageView.setImageBitmap(image);
            Uri imageU = Uri.parse(imageURI);
            Log.d("debugger",imageU.toString());

            ImageView imageV = getActivity().findViewById(R.id.inputImage);
            imageV.setVisibility(View.VISIBLE);
            imageV.setImageURI(null);
            imageV.setImageURI(imageU);

        }else{
            ImageView image = getActivity().findViewById(R.id.inputImage);
            image.setVisibility(View.GONE);
            //image.setImageURI(null);
            Log.d("debugger","none");
        }

        DisplayFragment(true);
        title.requestFocus();

        Button shareJournal = getActivity().findViewById(R.id.btnShare);
        shareJournal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedEntry != -1) {

                    Uri shareuri = Uri.parse(JournalTable.selectByID(db, selectedEntry).getmEntryImage());

                    Intent shareIntent = new Intent();
                    shareIntent.setAction(shareIntent.ACTION_SEND);
                    shareIntent.putExtra(shareIntent.EXTRA_TEXT, "Title: " + entry.getmEntryTitle() + "\n" + "Text:" + entry.getmEntryText()
                            + "\n" + "Date:" + entry.getmEntryDate() + "\n" + "Mood:" + moodAdapter.moods[entry.getmEntryMood()]);
                    if(!JournalTable.selectByID(db,selectedEntry).getmEntryImage().equals("")) {

                        shareIntent.putExtra(shareIntent.EXTRA_STREAM, shareuri);
                    }
                    shareIntent.setType("image/*");
                    shareIntent.addFlags(shareIntent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(Intent.createChooser(shareIntent, "Share entry..."));
                }
            }
        });

        Button btnDelete = getActivity().findViewById(R.id.btnDelete);
        Button btnShare = getActivity().findViewById(R.id.btnShare);

        btnDelete.setVisibility(View.VISIBLE);
        btnShare.setVisibility(View.VISIBLE);

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(title, InputMethodManager.SHOW_IMPLICIT);

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.btnAddEntry);
        //fab.setVisibility(View.GONE);
        mode = 2;
        selectedEntry = id;

    }
    public void UpdateEntry(int id){
        //Entry entry = new Entry();
        Entry entry = JournalTable.selectByID(db,id);

        EditText title = getActivity().findViewById(R.id.inputTitle);
        EditText text = getActivity().findViewById(R.id.inputText);
        TextView time = getActivity().findViewById(R.id.inputTime);
        entry.setmEntryTitle(title.getText().toString());
        entry.setmEntryText(text.getText().toString());
        entry.setmEntryTime(time.getText().toString());
        entry.setmEntryMood(moodSpinner.getSelectedItemPosition());

        //ImageView image = getActivity().findViewById(R.id.inputImage);
        //etPic(image, entry.getmEntryImage());

        //image.setVisibility(View.VISIBLE);
        //Log.d("debugger","null");
        entry.setmEntryImage(imageURI);

        //Log.d("FOUND","ID: "+entry.getmEntryID());
        //Log.d("FOUND","Title: "+entry.getmEntryTitle());

        JournalTable.update(db,entry,id);
        entries = JournalTable.selectByDate(db,entry.getmEntryDate());
        entryListAdapter = new JournalAdapter(getActivity().getApplicationContext(), R.layout.custom_list_layout, entries);
        myList.setAdapter(entryListAdapter);
    }
    public void DeleteEntry(int id){
        JournalTable.deleteById(db,id);
        //Log.d("FOUND","DELETING: " + id);

        entries = JournalTable.selectAll(db);
        entryListAdapter = new JournalAdapter(getActivity().getApplicationContext(), R.layout.custom_list_layout, entries);
        myList.setAdapter(entryListAdapter);
        if(entries.isEmpty()){
            getActivity().findViewById(R.id.helpEntries).setVisibility(View.VISIBLE);
            //Log.d("FOUND","EMPTY");
        }else{
            getActivity().findViewById(R.id.helpEntries).setVisibility(View.GONE);
        }
    }
    public void DisplayFragment(boolean state) {
        if (state) {
            fm.beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .show(fm.findFragmentById(R.id.entryFragment))
                    .commit();
        } else {
            fm.beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .hide(fm.findFragmentById(R.id.entryFragment))
                    .commit();
        }
    }

    void UpdateList(String date){
        entries = JournalTable.selectByDate(db,date);
        entryListAdapter = new JournalAdapter(getActivity().getApplicationContext(), R.layout.custom_list_layout, entries);
        myList.setAdapter(entryListAdapter);
        if(entries.isEmpty()){
            getActivity().findViewById(R.id.helpEntries).setVisibility(View.VISIBLE);
            //Log.d("FOUND","EMPTY");
            ImageView mImage = getActivity().findViewById(R.id.moodImage);
                    mImage.setBackgroundResource(R.drawable.neutral_face);
        }else{
            getActivity().findViewById(R.id.helpEntries).setVisibility(View.GONE);
        }
    }

    //for testing
    void ShowAll(){
        entries = JournalTable.selectAll(db);
        entryListAdapter = new JournalAdapter(getActivity().getApplicationContext(), R.layout.custom_list_layout, entries);
        myList.setAdapter(entryListAdapter);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    void Camera(){
        //☆☆☆☆☆☆add image part(don't forget changes about row 42-49 & Android manifest.xml) ☆☆☆☆☆☆
        final Button imageFromCamera = getActivity().findViewById(R.id.btnCamera);
        Button imageFromStorage = getActivity().findViewById(R.id.btnGallery);
        imageView = getActivity().findViewById(R.id.inputImage);
        imageFromStorage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                imageView.setVisibility(View.VISIBLE);
                Intent pickImage = new Intent(Intent.ACTION_PICK);
                File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                String pictureDirectoryPath = pictureDirectory.getPath();
                Uri data = Uri.parse(pictureDirectoryPath);
                pickImage.setDataAndType(data, "image/*");
                startActivityForResult(pickImage,IMAGE_GALLERY_REQUEST);
                //check for read permission
                if (ActivityCompat.checkSelfPermission(getContext(),Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                {
                    //Log.d("TAG", "Permission was previously granted, let's go!");
                    //write();
                    //read();
                }
                else
                {    //request  permission
                    String[] permissions = { Manifest.permission.READ_EXTERNAL_STORAGE };
                    ActivityCompat.requestPermissions(getActivity(), permissions, READ_REQUEST_CODE);
                }

            }
        });

        //set onclick listener on camera button

        imageFromCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                requestToTakeAPicture();
                //Log.d("frags","take");
            }
        });
    }

    //Request permissions at runtime
    private void requestToTakeAPicture()
    {

        requestPermissions( new String[] { Manifest.permission.CAMERA }, REQUEST_IMAGE_CAPTURE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {

        switch (requestCode)
        {
            case REQUEST_IMAGE_CAPTURE:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                // permission was granted, yay!
                {
                    takeAPicture();

                }
                else
                {
                    // permission denied, boo!

                }
                break;
        }
    }

    //Defer to the user’s camera app to return an image
    private void takeAPicture()
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that theres a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null)
        {        // Create the File where the photo should go

            try {
                File photoFile = createImageFile();
                Uri photoURI = FileProvider.getUriForFile(getActivity().getApplicationContext(), "assignment2.ashishr.utas.edu.au.journal", photoFile);

                //Entry entry = JournalTable.selectByID(db, selectedEntry);
                //entry.setmEntryImage(photoURI.toString());
                imageURI = photoURI.toString();
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                JournalFragment.this.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

            } catch (IOException ex) {

            }
        }
    }

    //createImageFile() defines a unique filename for the image (using the date)
    String mCurrentPhotoPath;
    private File createImageFile() throws IOException
    {
        // Create an image file name
        String timeStamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "MYIMAGE_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".png",         /* suffix */
                storageDir      /* directory */
        );
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    //Handle the result from the camera activity


    //setPic() decodes the image to a Bitmap and places it in an ImageView
    // overwrite onActivityResult to give back an image
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        //Log.d("resultback","resultback");
        if(resultCode == RESULT_OK)
        {

            if(requestCode == IMAGE_GALLERY_REQUEST)
            {
                //Log.d("debugger","here1" + requestCode);

                Uri imageUri= data.getData();
                InputStream inputStream;

                try {
                    inputStream = getActivity().getContentResolver().openInputStream(imageUri);
                    //get a bitmap from a stream
                    Bitmap image= BitmapFactory.decodeStream(inputStream);
                    imageView.setImageBitmap(image);
                    //store theuri into sqlite
                    //Entry entry = JournalTable.selectByID(db, selectedEntry);
                    //entry.setmEntryImage(imageUri.toString());
                    //Log.d("URIinput",entry.getmEntryImage());
                    //Log.d("debugger","here" + requestCode);
                    imageURI = imageUri.toString();


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(),"unable to get image",Toast.LENGTH_LONG).show();//show the user image failed to get
                }
            }
        }
        //Log.d("debugger","here1 " + requestCode);

        if(resultCode== RESULT_OK)
        {
            if(requestCode==REQUEST_IMAGE_CAPTURE)
            {
                Uri imageU = Uri.parse(imageURI);
                Log.d("debugger",imageU.toString());

                ImageView myImageView = getActivity().findViewById(R.id.inputImage);
                setPic(myImageView, mCurrentPhotoPath);
            }
        }
    }

    //setPic() decodes the image to a Bitmap and places it in an ImageView
    private void setPic(ImageView myImageView, String path)
    {
             /*
             // Get the dimensions of the View
              int targetW = myImageView.getWidth();
              int targetH = myImageView.getHeight();
             // Get the dimensions of the bitmap
             BitmapFactory.Options bmOptions = new BitmapFactory.Options();
             bmOptions.inJustDecodeBounds = true;
             BitmapFactory.decodeFile(path, bmOptions);
             int photoW = bmOptions.outWidth;
             int photoH = bmOptions.outHeight;
             // Determine how much to scale down the image
             int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
             // Decode the image file into a Bitmap sized to fill the View
             bmOptions.inJustDecodeBounds = false;
             bmOptions.inSampleSize = scaleFactor;
             bmOptions.inPurgeable = true;
             */
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        myImageView.setImageBitmap(bitmap);
        myImageView.setVisibility(View.VISIBLE);

    }
}
