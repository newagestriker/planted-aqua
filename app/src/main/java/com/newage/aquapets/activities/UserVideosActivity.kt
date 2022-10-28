package com.newage.aquapets.activities

import android.os.Bundle
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.newage.aquapets.R
import kotlinx.android.synthetic.main.activity_user_videos.*

class UserVideosActivity : YouTubeBaseActivity() {
    private lateinit var onInitializedListener: YouTubePlayer.OnInitializedListener
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_videos)
        onInitializedListener = object : YouTubePlayer.OnInitializedListener {
            override fun onInitializationSuccess(provider: YouTubePlayer.Provider, youTubePlayer: YouTubePlayer, b: Boolean) {
                youTubePlayer.loadPlaylist("PLAM79l8kWkXX7dUfP6i7rBG_9hQh3zwUS")
            }
            override fun onInitializationFailure(provider: YouTubePlayer.Provider, youTubeInitializationResult: YouTubeInitializationResult) {

            }
        }

        button2.setOnClickListener {
            youTubePlayerView.initialize(resources.getString(R.string.google_api_key_universal),onInitializedListener)
        }
    }
}