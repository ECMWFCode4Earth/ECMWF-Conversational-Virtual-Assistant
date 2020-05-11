package com._2horizon.cva.retrieval.event

import com._2horizon.cva.retrieval.confluence.dto.content.Content
import com._2horizon.cva.retrieval.confluence.dto.space.Space

/**
 * Created by Frank Lieber (liefra) on 2020-05-11.
 */
data class ConfluenceContentEvent( val spaceKey:String, val contentList: List<Content>)
data class ConfluenceSpacesEvent(val spacesList: List<Space>)
