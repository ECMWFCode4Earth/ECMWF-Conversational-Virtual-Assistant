package com._2horizon.cva.dialogflow.fulfillment.extensions

import com.google.cloud.dialogflow.v2beta1.Intent

/**
 * Created by Frank Lieber (liefra) on 2020-08-25.
 */
fun String.asIntentMessage(): Intent.Message {
    return Intent.Message.newBuilder()
        .setText(
            Intent.Message.Text.newBuilder().addText(this).build()
        ).build()
}

fun List<String>.asIntentMessage(): Intent.Message {
    return Intent.Message.newBuilder()
        .setText(
            Intent.Message.Text.newBuilder().addAllText(this).build()
        ).build()
}
