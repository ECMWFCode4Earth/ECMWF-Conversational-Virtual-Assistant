package com._2horizon.cva.dialogflow.manager.flowgraph

import com._2horizon.cva.dialogflow.manager.reporting.IntentHealthReportingService
import com.google.cloud.dialogflow.v2beta1.Intent
import it.uniroma1.dis.wsngroup.gexf4j.core.EdgeType
import it.uniroma1.dis.wsngroup.gexf4j.core.Gexf
import it.uniroma1.dis.wsngroup.gexf4j.core.Graph
import it.uniroma1.dis.wsngroup.gexf4j.core.Mode
import it.uniroma1.dis.wsngroup.gexf4j.core.Node
import it.uniroma1.dis.wsngroup.gexf4j.core.data.Attribute
import it.uniroma1.dis.wsngroup.gexf4j.core.data.AttributeClass
import it.uniroma1.dis.wsngroup.gexf4j.core.data.AttributeList
import it.uniroma1.dis.wsngroup.gexf4j.core.data.AttributeType
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.GexfImpl
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.StaxGraphWriter
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.data.AttributeListImpl
import reactor.core.publisher.Mono
import java.io.StringWriter
import javax.inject.Singleton

/**
 * Created by Frank Lieber (liefra) on 2020-09-19.
 */
@Singleton
class FlowGraphService(
    private val intentHealthReportingService: IntentHealthReportingService,
) {

    fun ecmwfFlowGraph(): Mono<String> {
        val allIntents: List<Intent> = intentHealthReportingService.listAllEcmwfIntents()
        return Mono.just(createDialogflowGraph(allIntents))
    }

    fun c3sFlowGraph(): Mono<String> {
        val allIntents: List<Intent> = intentHealthReportingService.listAllC3sIntents()
        return Mono.just(createDialogflowGraph(allIntents))
    }

    private fun createDialogflowGraph(allIntents: List<Intent>): String {

        val gexf: Gexf = GexfImpl().apply { setVisualization(true) }
        val graph: Graph = gexf.graph
        graph.setDefaultEdgeType(EdgeType.DIRECTED).mode = Mode.DYNAMIC
        val attrList: AttributeList = AttributeListImpl(AttributeClass.NODE)
        graph.attributeLists.add(attrList)
        val attSize: Attribute = attrList.createAttribute("size", AttributeType.INTEGER, "size")
        val attHasEvents: Attribute = attrList.createAttribute("category", AttributeType.STRING, "Has Events")

        // create nodes
        val nodes: List<Node> = allIntents.map { intent ->

            val events = intentHealthReportingService.responseEvents(intent)

            val nodeSize = if (events.isEmpty()) {
                10
            } else {
                events.size * 10
            }

            graph.createNode(intent.displayName).apply {
                label = intent.displayName
                size = 20f
                attributeValues.addValue(attSize, nodeSize.toString())
                    .addValue(attHasEvents, if (events.isEmpty()) "No Events" else "Contains Events")
            }
        }

        // set node relationships
        var edges = 0
        nodes.forEachIndexed { index: Int, aNode: Node ->
            val intent = allIntents[index]
            val events = intentHealthReportingService.responseEvents(intent)

            events.forEach { event ->
                val connectToNode = nodes.first { it.id == event.name }
                aNode.connectTo(edges++.toString(), connectToNode)
            }

        }

        return gexfToString(gexf)
    }

    private fun gexfToString(gexf: Gexf): String {
        val graphWriter = StaxGraphWriter()

        return StringWriter().use { sw ->
            graphWriter.writeToStream(gexf, sw, "UTF-8")
            sw.toString()
        }
    }
}
