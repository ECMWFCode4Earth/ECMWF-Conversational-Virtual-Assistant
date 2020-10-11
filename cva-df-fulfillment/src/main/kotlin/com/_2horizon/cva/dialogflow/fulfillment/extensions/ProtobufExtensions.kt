package com._2horizon.cva.dialogflow.fulfillment.extensions

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.protobuf.Struct
import com.google.protobuf.util.JsonFormat

/**
 * Created by Frank Lieber (liefra) on 2020-08-23.
 */
fun Struct.convertToJsonString(): String = JsonFormat.printer().print(this)

// inline fun <reified T> Struct.convertToObject(objectMapper: ObjectMapper): T {
//     val json =  JsonFormat.printer().print(this)
//     return objectMapper.readValue(json, T::class.java)
// }

// inline fun <reified T> Any.convertToStruct(objectMapper: ObjectMapper): Struct {
//     val json = objectMapper.writeValueAsString(this)
//     return Struct.newBuilder().apply { JsonFormat.parser().merge(json, this) }.build()
// }

inline fun <reified T> ObjectMapper.convertStructToObject(struct: Struct): T {
    val json = JsonFormat.printer().print(struct)
    return this.readValue(json, T::class.java)
}

fun ObjectMapper.convertObjectToStruct(any: Any): Struct {
    val json = this.writeValueAsString(any)
    return Struct.newBuilder().apply { JsonFormat.parser().merge(json, this) }.build()
}
