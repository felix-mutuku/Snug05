package com.snugjar.www.orderviewreview.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.snugjar.www.orderviewreview.Fragments.CategoriesFragment;
import com.snugjar.www.orderviewreview.R;

import java.util.ArrayList;
import java.util.List;

public class CategoriesActivity extends AppCompatActivity {
    String SCategory, SSupermarketName, SSupermarketID, SCountry;
    TabLayout tabs_layout;
    ViewPager viewPager;
    TextView back;
    ImageView search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        viewPager = findViewById(R.id.viewpager);
        tabs_layout = findViewById(R.id.tabs_layout);
        back = findViewById(R.id.back);
        search = findViewById(R.id.search);

        //get category selected by the user and the respective supermarket
        SCategory = getIntent().getStringExtra("category_selected");
        SSupermarketName = getIntent().getStringExtra("supermarketName");
        SSupermarketID = getIntent().getStringExtra("supermarketID");
        SCountry = getIntent().getStringExtra("country");

        if (!SSupermarketID.equals("") && !SSupermarketName.equals("") && !SCategory.equals("")) {
            initialize();
        } else {
            onBackPressed();
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go back
                onBackPressed();
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open search activity
                goToSearch();
            }
        });
    }

    private void goToSearch() {
        //open activity ya search
        Intent intent = new Intent(CategoriesActivity.this, SearchActivity.class);
        intent.putExtra("id", SSupermarketID);
        intent.putExtra("name", SSupermarketName);
        startActivity(intent);
    }

    private void initialize() {
        back.setText(SSupermarketName);
        //check category selected and set all sub-categories needed
       /* switch (SCategory) {
            case "offers":
                offersViewPager(viewPager);
                tabs_layout.setupWithViewPager(viewPager);
                break;
            case "baby_kids":
                baby_kidsViewPager(viewPager);
                tabs_layout.setupWithViewPager(viewPager);
                break;
            case "beauty_cosmetics":
                beauty_cosmeticsViewPager(viewPager);
                tabs_layout.setupWithViewPager(viewPager);
                break;
            case "canned_goods":
                canned_goodsViewPager(viewPager);
                tabs_layout.setupWithViewPager(viewPager);
                break;
            case "cleaning":
                cleaningViewPager(viewPager);
                tabs_layout.setupWithViewPager(viewPager);
                break;
            case "cooking_oil":
                cooking_oilViewPager(viewPager);
                tabs_layout.setupWithViewPager(viewPager);
                break;
            case "dairy":
                dairyViewPager(viewPager);
                tabs_layout.setupWithViewPager(viewPager);
                break;
            case "drinks":
                drinksViewPager(viewPager);
                tabs_layout.setupWithViewPager(viewPager);
                break;
            case "food_cupboard":
                food_cupboardViewPager(viewPager);
                tabs_layout.setupWithViewPager(viewPager);
                break;
            case "fresh_food":
                fresh_foodViewPager(viewPager);
                tabs_layout.setupWithViewPager(viewPager);
                break;
            case "frozen":
                frozenViewPager(viewPager);
                tabs_layout.setupWithViewPager(viewPager);
                break;
            case "health_wellness":
                health_wellnessViewPager(viewPager);
                tabs_layout.setupWithViewPager(viewPager);
                break;
            case "household":
                householdViewPager(viewPager);
                tabs_layout.setupWithViewPager(viewPager);
                break;
            case "kitchen_dining":
                kitchen_diningViewPager(viewPager);
                tabs_layout.setupWithViewPager(viewPager);
                break;
            case "office_supplies":
                office_suppliesViewPager(viewPager);
                tabs_layout.setupWithViewPager(viewPager);
                break;
            case "sauces":
                saucesViewPager(viewPager);
                tabs_layout.setupWithViewPager(viewPager);
                break;
            case "snacks":
                snacksViewPager(viewPager);
                tabs_layout.setupWithViewPager(viewPager);
                break;
            case "toiletries":
                toiletriesViewPager(viewPager);
                tabs_layout.setupWithViewPager(viewPager);
                break;
            default:
                onBackPressed();
                break;
        }*/
    }

   /* private void offersViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.all)), "All");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.weekly)), "Weekly");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.monthly)), "Monthly");
        viewPager.setAdapter(adapter);
    }

    private void baby_kidsViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.all)), "All");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.baby_feeding)), "Baby Feeding");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.diapers)), "Diapers");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.baby_food)), "Baby Food");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.crayons)), "Crayons & Art Kits");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.games)), "Games & Toys");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.baby_wipes)), "Baby Wipes");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.kids_toiletries)), "Kid's Toiletries");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.kids_furniture)), "Kid's Furniture");
        viewPager.setAdapter(adapter);
    }

    private void beauty_cosmeticsViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.all)), "All");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.cotton_wool)), "Cotton Wool & Buds");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.facial_care)), "Facial Care");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.lip_care)), "Lip Care");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.make_up)), "Make Up");
        viewPager.setAdapter(adapter);
    }

    private void canned_goodsViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.all)), "All");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.tomato_paste)), "Tomato Paste");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.coconut_milk)), "Coconut Milk");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.canned_fruit)), "Canned Fruit & Vegetables");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.canned_beans)), "Canned Beans & Peas");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.tinned_tomatoes)), "Tinned Tomatoes");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.canned_fish)), "Canned Fish & Meat");
        viewPager.setAdapter(adapter);
    }

    private void cleaningViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.all)), "All");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.disinfectants)), "Disinfectants");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.brushes)), "Brushes, Mops & Buckets");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.surface_cleaners)), "Surface Cleaners");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.bathroom)), "Bathroom & Toilet Cleaners");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.dish)), "Dish & Kitchen Cleaners");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.sponges)), "Sponges & Scourers");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.rubber_gloves)), "Rubber Gloves");
        viewPager.setAdapter(adapter);
    }

    private void cooking_oilViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.all)), "All");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.cooking_fat)), "Cooking Fat");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.olive_oil)), "Olive Oil");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.specialty_oils)), "Specialty Oils & Cooking Wine");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.vegetable_oil)), "Groundnut & Vegetable Oil");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.corn_oil)), "Corn & Canola Oil");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.sunflower_oil)), "Soya & Sunflower Oil");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.ghee)), "Ghee");
        viewPager.setAdapter(adapter);
    }

    private void dairyViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.all)), "All");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.yoghurt)), "Yoghurt & Desserts");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.milk)), "Milk");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.milk_yoghurt)), "Milk & Yoghurt Drinks");
        viewPager.setAdapter(adapter);
    }

    private void drinksViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.all)), "All");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.choco_drinks)), "Choco Drinks");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.non_alcoholic)), "Non-alcoholic Wine");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.water)), "Water");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.energy_drinks)), "Energy Drinks");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.soft_drinks)), "Soft Drinks");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.fruit_juice)), "Fruit Juice & Flavoured Drinks");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.tea)), "Tea");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.coffee)), "Coffee");
        viewPager.setAdapter(adapter);
    }

    private void food_cupboardViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.all)), "All");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.salt_spices)), "Salt & Spices");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.olives_pickles)), "Olives & Pickles");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.breakfast_cereals)), "Breakfast Cereals");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.jams)), "Jams & Spreads");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.sugar)), "Sugar & Sweeteners");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.grains)), "Beans, Seeds & Grains");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.cake_bake)), "Cake, Bake & Pancake");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.custard)), "Custard & Jelly");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.soup)), "Soup");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.rice_pasta)), "Rice, Pasta & Noodles");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.ugali)), "Ugali & Porridge");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.ready_to_eat)), "Ready To Eat");
        viewPager.setAdapter(adapter);
    }

    private void fresh_foodViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.all)), "All");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.bread)), "Bread");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.cheese)), "Cheese");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.eggs)), "Eggs");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.fruits)), "Fruits");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.meat)), "Meat & Poultry");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.sausages)), "Sausages, Bacon & Cold Cuts");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.sea_food)), "Sea Food");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.bakery)), "Bakery");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.vegetables)), "Vegetables");
        viewPager.setAdapter(adapter);
    }

    private void frozenViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.all)), "All");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.butter)), "Butter & Margarine");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.ready_meals)), "Ready Meals");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.ice_cream)), "Ice Cream");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.chips)), "Chips");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.assorted_vegetables)), "Assorted Vegetables");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.mixed_vegetables)), "Mixed Vegetables");
        viewPager.setAdapter(adapter);
    }

    private void health_wellnessViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.all)), "All");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.vitamins)), "Vitamins & Supplements");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.allergy)), "Allergy & Infections");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.stomach)), "Stomach & Bowel");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.headaches)), "Headaches & Pain");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.coughing)), "Coughing & Sneezing");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.contraception)), "Contraception & Sexual Pleasure");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.first_aid)), "First Aid & Test Kits");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.hot_water_bottle)), "Hot Water Bottle");
        viewPager.setAdapter(adapter);
    }

    private void householdViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.all)), "All");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.air_freshener)), "Air Freshener & Fragrances");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.batteries)), "Batteries & Light Bulbs");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.bikes)), "Bikes & Pumps");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.car_care)), "Car Care");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.electronics)), "Electronics");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.facial_tissue)), "Facial Tissue");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.furniture)), "Furniture");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.hand_wash)), "Hand Wash & Sanitisers");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.insecticides)), "Insecticides");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.laundry)), "Laundry");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.pets)), "Pets");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.shoe_polish)), "Shoe Polish");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.toilet_tissue)), "Toilet Tissue");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.tools)), "Tools & Outdoors");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.waste_bins)), "Waste Bins & Bin Bags");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.beds_mattresses)), "Beds, Mattresses & Bedding");
        viewPager.setAdapter(adapter);
    }

    private void kitchen_diningViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.all)), "All");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.kitchen_utensils)), "Kitchen Utensils");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.cutlery)), "Disposable Plates & Cutlery");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.kitchen_towels)), "Kitchen Towels & Serviettes");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.cookware)), "Cookware & Tableware");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.drinking_glasses)), "Drinking Glasses");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.food_containers)), "Food Containers");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.foil)), "Foil & Cling Film");
        viewPager.setAdapter(adapter);
    }

    private void office_suppliesViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.all)), "All");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.notepads)), "Notepads");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.printing_paper)), "Printing Paper");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.general_stationery)), "General Stationery");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.filing)), "Filing & Storage");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.pens)), "Pens & Pencils");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.stationery_sets)), "Stationery Sets");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.news_papers)), "News Papers");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.bibles)), "Bibles & Hymn Books");
        viewPager.setAdapter(adapter);
    }

    private void saucesViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.all)), "All");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.table_sauces)), "Table Sauces");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.salad_dressing)), "Salad Dressing");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.cooking_cream)), "Cooking & Whipping Cream");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.vinegar)), "Vinegar");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.cooking_sauces)), "Cooking Sauces");
        viewPager.setAdapter(adapter);
    }

    private void snacksViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.all)), "All");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.biscuits)), "Biscuits & Wafers");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.chocolates)), "Chocolates");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.cocktail_snacks)), "Cocktail Snacks");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.energy_bar)), "Energy & Granola Bars");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.dried_fruit)), "Nuts & Dried Fruit");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.popcorn)), "Popcorn");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.potato_crisps)), "Potato Crisps");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.chewing_gum)), "Sweets & Chewing Gum");
        viewPager.setAdapter(adapter);
    }

    private void toiletriesViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.all)), "All");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.feminine_care)), "Feminine Care");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.hair_care)), "Hair Care");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.oral_care)), "Oral Care");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.women_shaving)), "Women's Shaving & Hair Removal");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.deodorants)), "Deodorants & Body Sprays");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.colognes_for_men)), "Colognes For Men");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.moisturisers)), "Moisturisers & Lotions");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.hand_lotion)), "Hand Lotion");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.perfumes_for_women)), "Perfumes For Women");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.bath_time)), "Bath Time");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.men_shaving)), "Men's Shaving");
        adapter.addFrag(new CategoriesFragment(SCountry, SSupermarketID, SSupermarketName, SCategory, getString(R.string.adult_diapers)), "Adult Diapers");
        viewPager.setAdapter(adapter);
    }*/

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
