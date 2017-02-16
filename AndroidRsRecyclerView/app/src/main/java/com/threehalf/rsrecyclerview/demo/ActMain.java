package com.threehalf.rsrecyclerview.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActMain extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_vlv, R.id.btn_hlv, R.id.btn_vgv, R.id.btn_hgv, R.id.btn_vsgv, R.id.btn_hsgv, R.id.btn_rlv, R.id.btn_rgv, R.id.btn_rsgv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_vlv:
                Intent intent = new Intent(ActMain.this, ActListView.class);
                intent.putExtra("isVertical", true);
                startActivity(intent);
                break;
            case R.id.btn_hlv:
                Intent intent2 = new Intent(ActMain.this, ActListView.class);
                intent2.putExtra("isVertical", false);
                startActivity(intent2);
                break;
            case R.id.btn_vgv:
                Intent intent3 = new Intent(ActMain.this, ActGridView.class);
                intent3.putExtra("isVertical", true);
                startActivity(intent3);
                break;
            case R.id.btn_hgv:
                Intent intent4 = new Intent(ActMain.this, ActGridView.class);
                intent4.putExtra("isVertical", false);
                startActivity(intent4);
                break;
            case R.id.btn_vsgv:
                Intent intent5 = new Intent(ActMain.this, ActStaggeredGridView.class);
                intent5.putExtra("isVertical", true);
                startActivity(intent5);
                break;
            case R.id.btn_hsgv:
                Intent intent6 = new Intent(ActMain.this, ActStaggeredGridView.class);
                intent6.putExtra("isVertical", false);
                startActivity(intent6);
                break;
            case R.id.btn_rlv:
                Intent intent7 = new Intent(ActMain.this, ActRefreshListView.class);
                startActivity(intent7);
                break;
            case R.id.btn_rgv:
                Intent intent8 = new Intent(ActMain.this, ActRefreshGridView.class);
                startActivity(intent8);
                break;
            case R.id.btn_rsgv:
                Intent intent9 = new Intent(ActMain.this, ActRefreshStaggeredGridView.class);
                startActivity(intent9);

                break;
        }
    }
}
