package com._2horizon.cva.dialogflow.manager.event

import com._2horizon.cva.dialogflow.manager.dialogflow.model.DialogflowFaqConfigModel

/**
 * Created by Frank Lieber (liefra) on 2020-08-02.
 */
data class DialogflowFaqConfigModelEvent(val dfModels: List<DialogflowFaqConfigModel>)
