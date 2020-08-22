package com._2horizon.cva.dialogflow.manager.dialogflow.model

/**
 * Created by Frank Lieber (liefra) on 2020-08-02.
 */
data class DialogflowFaqConfigModel(
    val intentId: String,
    val intentName: String,
    val trainingPhrases: Set<String>,
    val responses: Set<String>,
    val inputContexts: Set<DialogflowContext> = emptySet(),
    val outputContexts: Set<DialogflowContext> = emptySet(),
    val events: List<String> = emptyList()
)

data class DialogflowContext(
    val contextName: String,
    val lifespan: Int = 5
)
