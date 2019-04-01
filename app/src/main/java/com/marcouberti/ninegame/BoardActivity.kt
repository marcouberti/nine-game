package com.marcouberti.ninegame

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.marcouberti.ninegame.ui.board.BoardFragment

class BoardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.board_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, BoardFragment.newInstance())
                .commitNow()
        }
    }

}
