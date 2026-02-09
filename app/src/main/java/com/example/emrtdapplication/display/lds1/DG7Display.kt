package com.example.emrtdapplication.display.lds1

import android.content.Context
import android.graphics.BitmapFactory
import android.widget.ImageView
import android.widget.LinearLayout
import com.example.emrtdapplication.CreateView
import com.example.emrtdapplication.EMRTD
import com.example.emrtdapplication.R

object DG7Display : CreateView() {
    override fun <T : LinearLayout> createView(context: Context, parent: T) {
        if (EMRTD.ldS1Application.dg7.tlvS == null) return
        for (signature in EMRTD.ldS1Application.dg7.tlvS) {
            val box = LinearLayout(context)
            box.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            if (alternate) {
                box.setBackgroundColor(context.resources.getColor(R.color.gray, null))
            } else {
                box.setBackgroundColor(context.resources.getColor(R.color.black, null))
            }
            alternate = !alternate
            parent.addView(box)
            val image = signature.displaySignature.imageInputStream.readAllBytes()
            val bitmap = BitmapFactory.decodeByteArray(image, 0, image.size)
            val view = ImageView(context)
            view.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            view.setImageBitmap(bitmap)
            box.addView(view)
        }
    }
}