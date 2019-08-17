package com.rizieq.drinkshop;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.ipaulpro.afilechooser.utils.FileUtils;
import com.nex3z.notificationbadge.NotificationBadge;
import com.rizieq.drinkshop.Adapter.CategoryAdapter;
import com.rizieq.drinkshop.Database.DataSource.CartRepository;
import com.rizieq.drinkshop.Database.DataSource.FavoriteRepository;
import com.rizieq.drinkshop.Database.Local.CartDataSource;
import com.rizieq.drinkshop.Database.Local.EDMTRoomDatabase;
import com.rizieq.drinkshop.Database.Local.FavoriteDataSource;
import com.rizieq.drinkshop.Model.Banner;
import com.rizieq.drinkshop.Model.Category;
import com.rizieq.drinkshop.Model.Drink;
import com.rizieq.drinkshop.Model.User;
import com.rizieq.drinkshop.Retrofit.IDrinkShopAPI;
import com.rizieq.drinkshop.Utils.Common;
import com.rizieq.drinkshop.Utils.ProggressRequestBody;
import com.rizieq.drinkshop.Utils.UploadCallBack;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, UploadCallBack {

    private static final int PICK_FILE_REQUEST = 1222;
    TextView txt_name, txt_phone;
    SessionManager sm;
    SliderLayout sliderLayout;

    IDrinkShopAPI mService;

    RecyclerView lst_menu;

    NotificationBadge badge;
    ImageView cart_icon;


    CircleImageView img_avatar;

    Uri selectedFileUri;



    //RxJava
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);

        mService = Common.getAPI();

        lst_menu = findViewById(R.id.lst_menu);
        lst_menu.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        lst_menu.setHasFixedSize(true);

        sliderLayout = (SliderLayout) findViewById(R.id.slider);

        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);


        sm = new SessionManager(HomeActivity.this);

        HashMap<String, String> map = sm.getDataLogin();


        View headerView = navigationView.getHeaderView(0);
        txt_name = headerView.findViewById(R.id.tv_name);
        txt_phone = headerView.findViewById(R.id.tv_phone);
        img_avatar = headerView.findViewById(R.id.img_avatar);


        // Event OnClick IMAGE AVATAR
        img_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        // Set Info User
        txt_name.setText(map.get(sm.KEY_NAME));
        txt_phone.setText(map.get(sm.KEY_PHONE));


        /*Log.d("BERHASIL_LOAD ",map.get(sm.KEY_PHONE));*/

        // Set Avatar
        if (!TextUtils.isEmpty(map.get(sm.KEY_AVATAR_URL)))
        {
            Picasso.with(this)
                    .load(new StringBuilder(Common.BASE_URL)
                    .append("user_avatar/")
                    .append(map.get(sm.KEY_AVATAR_URL)).toString())
                    .into(img_avatar);
        }

        // Get Banner
        getBannerImage();

        // Get Menu
        getMenu();

        // Save topping newest Topping List
        getToppingList();

        // Init SQLITE Room Database
        initDB();

        sm.checkLogin();
    }



    private void chooseImage() {

        // TODO to active FileUtils, we need add aFileChooser module to our project
        startActivityForResult(Intent.createChooser(FileUtils.createGetContentIntent(),"Select a file"),
                PICK_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
        {
            if (requestCode ==PICK_FILE_REQUEST)
            {
                if (data != null)
                {
                    selectedFileUri = data.getData();
                    if (selectedFileUri != null && !selectedFileUri.getPath().isEmpty())
                    {
                        img_avatar.setImageURI(selectedFileUri);
                        uploadFile();
                    }
                    else
                        Toast.makeText(this, "Cannot upload file to server", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void uploadFile() {
        final HashMap<String, String> map = sm.getDataLogin();
        if (selectedFileUri != null)
        {
            File file = FileUtils.getFile(this,selectedFileUri);
            String fileName = new StringBuilder(map.get(sm.KEY_PHONE)) // get phone kalo gk dapet ambil dari session manager
                    .append(FileUtils.getExtension(file.toString()))
                    .toString();

            ProggressRequestBody requestFile = new ProggressRequestBody(file,this);

            final MultipartBody.Part body = MultipartBody.Part.createFormData("uploaded_file",fileName,requestFile);

            final MultipartBody.Part userPhone = MultipartBody.Part.createFormData("phone",map.get(sm.KEY_PHONE));

            new Thread(new Runnable() {
                @Override
                public void run() {
                    mService.uploadFile(userPhone,body)
                            .enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {

                                    if (response.isSuccessful()){

                                        final String responUpload = response.body();

                                        mService.getUserInformation(map.get(sm.KEY_PHONE))
                                                .enqueue(new Callback<User>() {
                                                    @Override
                                                    public void onResponse(Call<User> call, Response<User> response) {

                                                        User user = response.body();
                                                        sm.storeLogin(user.getPhone(),
                                                                user.getName(),
                                                                user.getAddress(),
                                                                user.getBrithdate(),
                                                                user.getAvatarUrl());

                                                        Toast.makeText(HomeActivity.this, responUpload, Toast.LENGTH_SHORT).show();

                                                    }

                                                    @Override
                                                    public void onFailure(Call<User> call, Throwable t) {

                                                        Log.d("ONFAILURE_USER_MAIN ",t.getLocalizedMessage());
                                                        Toast.makeText(HomeActivity.this, "Failed To Save Data", Toast.LENGTH_SHORT).show();
                                                    }
                                                });


                                    }




                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    Toast.makeText(HomeActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }).start();
        }
    }

    private void initDB() {
        Common.edmtRoomDatabase = EDMTRoomDatabase.getInstance(this);
        Common.cartRepository = CartRepository.getInstance(CartDataSource.getInstance(Common.edmtRoomDatabase.cartDAO()));
        Common.favoriteRepository = FavoriteRepository.getInstance(FavoriteDataSource.getInstance(Common.edmtRoomDatabase.favoritetDAO()));
    }

    private void getToppingList() {
        compositeDisposable.add(mService.getDrink(Common.TOPPING_MENU_ID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Drink>>() {
                    @Override
                    public void accept(List<Drink> drinks) throws Exception {
                        Common.toppingList = drinks;
                    }
                }));
    }

    private void getMenu() {
        compositeDisposable.add(mService.getMenu()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Category>>() {
                    @Override
                    public void accept(List<Category> categories) throws Exception {
                        displayMenu(categories);
                    }
                }));

    }

    private void displayMenu(List<Category> categories) {
        CategoryAdapter adapter = new CategoryAdapter(this, categories);
        lst_menu.setAdapter(adapter);
    }


    private void getBannerImage() {
        compositeDisposable.add(mService.getBanners()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Banner>>() {
                    @Override
                    public void accept(List<Banner> banners) throws Exception {
                        displayImage(banners);
                    }
                }));
    }


    @Override
    protected void onDestroy() {
        compositeDisposable.dispose();
        super.onDestroy();
    }

    private void displayImage(List<Banner> banners) {
        HashMap<String, String> bannerMap = new HashMap<>();
        for (Banner item : banners) {
            String nama = item.getLink();
            Log.d("LINK_GAMBAR ", nama);
            bannerMap.put(item.getName(), item.getLink());
        }


        for (String name : bannerMap.keySet()) {
            TextSliderView textSliderView = new TextSliderView(this);
            textSliderView.description(name)
                    .image(bannerMap.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit);

            sliderLayout.addSlider(textSliderView);
        }


    }

    boolean isBackButtonClicked = false;
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (isBackButtonClicked){
                super.onBackPressed();
                return;
            }
            this.isBackButtonClicked = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_action_bar, menu);
        View view = menu.findItem(R.id.cart_menu).getActionView();

        badge = (NotificationBadge) view.findViewById(R.id.notifbadge);
        cart_icon = (ImageView) view.findViewById(R.id.cart_icon);
        cart_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,CartActivity.class));
            }
        });

        updateCartCount();
        return true;
    }

    private void updateCartCount() {
        if (badge == null) return;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (Common.cartRepository.countCartItems() == 0)
                    badge.setVisibility(View.INVISIBLE);
                else {
                    badge.setVisibility(View.VISIBLE);
                    badge.setText(String.valueOf(Common.cartRepository.countCartItems()));
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.cart_menu) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_sign_out) {
            // Create confirm Dialog

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Exit Aplication");
            builder.setMessage("Do you want to exit this aplication ?");

            builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    sm.logout();
                    sm.checkLogin();
                    finish();
                    /*// Clear all Activity
                    Intent intent = new Intent(HomeActivity.this,MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    startActivity(intent);
                    finish();*/
                }
            });

            builder.setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            // Show Dialog
            builder.show();

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartCount();
        isBackButtonClicked = false;


    }

    @Override
    public void onProggressUpdate(int percentage) {

    }


}
