package com._2horizon.cva.nlp

/**
 * Created by Frank Lieber (liefra) on 2020-08-27.
 */
data class SignificantTermsReceivedEvent(val datastore: Datastore, val significantTerms: List<SignificantTerm>)
