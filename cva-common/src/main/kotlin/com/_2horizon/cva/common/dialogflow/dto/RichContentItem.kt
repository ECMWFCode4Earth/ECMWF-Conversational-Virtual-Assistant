package com._2horizon.cva.common.dialogflow.dto

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

/**
 * Created by Frank Lieber (liefra) on 2020-07-05.
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = RichContentInfoItem::class, name = "info"),
    JsonSubTypes.Type(value = RichContentButtonItem::class, name = "button"),
    JsonSubTypes.Type(value = RichContentListItem::class, name = "list"),
    JsonSubTypes.Type(value = RichContentDividerItem::class, name = "divider"),
    JsonSubTypes.Type(value = RichContentImageItem::class, name = "image"),
    JsonSubTypes.Type(value = RichContentAccordionItem::class, name = "accordion"),
    JsonSubTypes.Type(value = RichContentSuggestionChipsItem::class, name = "chips"),
    JsonSubTypes.Type(value = RichContentDividerItem::class, name = "description"),
)
interface RichContentItem {
    val type: String
}
