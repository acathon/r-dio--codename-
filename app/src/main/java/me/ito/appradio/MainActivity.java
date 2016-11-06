package me.ito.appradio;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import java.io.IOException;
import java.util.ArrayList;

import it.moondroid.coverflow.components.ui.containers.FeatureCoverFlow;

public class MainActivity extends AppCompatActivity implements MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    private final static String MOSAIQUEFM_LIVE = "http://webradio.mosaiquefm.net:8000/mosalive";
    private final static String SHEMSFM_LIVE = "http://stream6.tanitweb.com/shems";
    private final static String IFM_LIVE = "http://5.135.138.182:8000/direct";
    private final static String CAPFM_LIVE = "http://stream8.tanitweb.com/capfm";
    private final static String JAWHARAFM_LIVE = "http://streaming2.toutech.net:8000/jawharafm";
    private final static String OASISFM_LIVE = "http://stream6.tanitweb.com/oasis";
    private final static String SABRAFM_LIVE = "http://stream6.tanitweb.com/sabrafm";
    private final static String ZITOUNAFM_LIVE = "http://stream8.tanitweb.com/zitounafm"; //http://www.zitounafm.net/zitouna.asx
    private final static String EXPRESSFM_LIVE = "http://stream6.tanitweb.com/expressfm";

    private Toolbar mToolbar;
    private FeatureCoverFlow mCoverFlow;
    private RadioAdapter mAdapter;
    private ArrayList<RadioEntity> mData = new ArrayList<>(0);
    private TextSwitcher mTitle;
    private MediaPlayer player = null;
    private SeekBar volumeBar;
    private AudioManager volumeMgr;
    private Toast appNote;
    private ImageButton btnPlay;
    private String stationRadio;
    private String station;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (connected() == false)
            appNote.makeText(getApplicationContext(), "Pas de connexion Internet !", appNote.LENGTH_LONG).show();


        btnPlay = (ImageButton) findViewById(R.id.btn_play_pause);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //appNote.makeText(getApplicationContext(), stationRadio, appNote.LENGTH_LONG).show();
                if (player != null) {
                    player.reset();
                    player.stop();
                    player.release();
                    player = null;
                    btnPlay.setImageResource(R.drawable.btn_play);
                } else
                    playerInit(station);
            }
        });

        mData.add(new RadioEntity(R.drawable.mosaiquefm, R.string.mosaiquefm));
        mData.add(new RadioEntity(R.drawable.ifm, R.string.ifm));
        mData.add(new RadioEntity(R.drawable.capfm, R.string.capfm));
        // mData.add(new RadioEntity(R.drawable.radiotunisienne, R.string.radiotunisienne));
        mData.add(new RadioEntity(R.drawable.jawharafm, R.string.jawharafm));
        mData.add(new RadioEntity(R.drawable.oasisifm, R.string.oasisfm));
        mData.add(new RadioEntity(R.drawable.sabrafm, R.string.sabrafm));
        mData.add(new RadioEntity(R.drawable.zitounafm, R.string.zitounafm));
        mData.add(new RadioEntity(R.drawable.shemsfm, R.string.shemsfm));
        mData.add(new RadioEntity(R.drawable.expressfm, R.string.expressfm));

        mTitle = (TextSwitcher) findViewById(R.id.title);
        mTitle.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                TextView textView = (TextView) inflater.inflate(R.layout.item_title, null);
                return textView;
            }
        });
        Animation in = AnimationUtils.loadAnimation(this, R.anim.slide_in_top);
        Animation out = AnimationUtils.loadAnimation(this, R.anim.slide_out_bottom);
        mTitle.setInAnimation(in);
        mTitle.setOutAnimation(out);

        mAdapter = new RadioAdapter(this);
        mAdapter.setData(mData);
        mCoverFlow = (FeatureCoverFlow) findViewById(R.id.coverflow);
        mCoverFlow.setAdapter(mAdapter);

        mCoverFlow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Toast.makeText(MainActivity.this, getResources().getString(mData.get(position).titleResId), Toast.LENGTH_SHORT).show();
                // switch (getResources().getString(mData.get(position).titleResId)) {
                //    case "Mosaique FM":
                //        playerInit(MOSAIQUEFM_LIVE);
                //        break;
                //    case "Shems FM":
                //        playerInit(SHEMSFM_LIVE);
                //        break;
                //    case "Cap FM":
                //        playerInit(CAPFM_LIVE);
                //        break;
                //    case "IFM":
                //        playerInit(IFM_LIVE);
                //        break;
                //    case "Jawhara FM":
                //        playerInit(JAWHARAFM_LIVE);
                //        break;
                //    case "Oasis FM":
                //        playerInit(OASISFM_LIVE);
                //     break;
                //  case "Zitouna FM":
                //       playerInit(ZITOUNAFM_LIVE);
                //        break;
                //    case "Express FM":
                //        playerInit(EXPRESSFM_LIVE);
                //       break;
                //    case "Sabra FM":
                //        playerInit(SABRAFM_LIVE);
                //       break;
                // }
            }
        });

        mCoverFlow.setOnScrollPositionListener(new FeatureCoverFlow.OnScrollPositionListener() {
            @Override
            public void onScrolledToPosition(int i) {
                mTitle.setText(getResources().getString(mData.get(i).titleResId));
                stationRadio = getResources().getString(mData.get(i).titleResId);
                btnPlay.setImageResource(R.drawable.btn_play);
                linkSwitch(stationRadio);
            }

            @Override
            public void onScrolling() {
                mTitle.setText("");
                if (player != null) {
                    player.reset();
                    player.stop();
                    player.release();
                    player = null;
                }
                btnPlay.setSoundEffectsEnabled(true);
                btnPlay.setImageResource(R.drawable.btn_play);
            }
        });

        volumeBar = (SeekBar) findViewById(R.id.volumeBar);
        volumeMgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        try {
            volumeBar.setMax(volumeMgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            volumeBar.setProgress(volumeMgr.getStreamVolume(AudioManager.STREAM_MUSIC));
            volumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    volumeMgr.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean connected() {
        ConnectivityManager connectMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectMgr.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    public void playerInit(String radio) {
        // if (player != null && player.isPlaying()) {
        //    player.stop();
        //    player.setOnCompletionListener(this);
        //    playerInit(radio);
        // } else {
        try {
            player = new MediaPlayer();
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setOnBufferingUpdateListener(this);
            player.setDataSource(radio);
            player.setOnPreparedListener(this);
            player.prepareAsync();
            btnPlay.setImageResource(R.drawable.btn_pause);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // }
    }

    public void linkSwitch(String radio) {
        switch (radio) {
            case "Mosaique FM":
                station = MOSAIQUEFM_LIVE;
                break;
            case "Shems FM":
                station = SHEMSFM_LIVE;
                break;
            case "Cap FM":
                station = CAPFM_LIVE;
                break;
            case "IFM":
                station = IFM_LIVE;
                break;
            case "Jawhara FM":
                station = JAWHARAFM_LIVE;
                break;
            case "Oasis FM":
                station = OASISFM_LIVE;
                break;
            case "Zitouna FM":
                station = ZITOUNAFM_LIVE;
                break;
            case "Express FM":
                station = EXPRESSFM_LIVE;
                break;
            case "Sabra FM":
                station = SABRAFM_LIVE;
                break;
        }
    }


    @Override
    public void onCompletion(MediaPlayer mp) {
        mp.reset();
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }


    @Override
    public void onStop() {
        player.reset();
        player.stop();
        player.release();
        super.onStop();
    }

    @Override
    public void onPause() {
        player.stop();
        player.reset();
        player.release();
        super.onPause();
    }
}