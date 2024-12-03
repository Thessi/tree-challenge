package at.tree.challenge.service

import at.tree.challenge.exception.EdgeExistsException
import at.tree.challenge.exception.EdgeNotFoundException
import at.tree.challenge.exception.LoopException
import at.tree.challenge.persistence.TreeRepository
import at.tree.challenge.service.model.Edge
import at.tree.challenge.service.model.Node
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TreeService @Autowired constructor(val treeRepository: TreeRepository) {
    fun getTree(edgeId: Long, depth: Long): Node {
        val edges = treeRepository.getTree(edgeId, depth)

        if (edges.isEmpty()) {
            throw EdgeNotFoundException()
        }

        val nodes = buildTree(edges)
        val rootNode = nodes[edgeId]!!
        return rootNode
    }

    @Transactional
    fun createEdge(edge: Edge) {
        val existingEdge = treeRepository.getEdge(edge)
        if (existingEdge != null) {
            throw EdgeExistsException()
        }

        detectLoop(edge)
        treeRepository.createEdge(edge)
    }

    @Transactional
    fun deleteEdge(edge: Edge) {
        val existingEdge = treeRepository.getEdge(edge)
        if (existingEdge == null) {
            throw EdgeNotFoundException()
        }

        treeRepository.deleteEdge(edge)
    }

    private fun detectLoop(edge: Edge) {
        // TODO: Extend to also include more complex loops

        if (edge.fromId == edge.toId) {
            throw LoopException()
        }

        val existingEdgesFromChild = treeRepository.getEdgesByFromId(edge.toId)
        if (existingEdgesFromChild.any { it.toId == edge.fromId }) {
            throw LoopException()
        }
    }

    // Saves all nodes separately, which are connected in the loop. In the end, all nodes should be contained and
    // connected to the rest of the tree.
    private fun buildTree(edges: List<Edge>): HashMap<Long, Node> {
        val nodes = HashMap<Long, Node>()
        edges.forEach {
            nodes.putIfAbsent(it.fromId, Node(it.fromId))
            nodes.putIfAbsent(it.toId, Node(it.toId))

            val parentNode = nodes[it.fromId]
            val childNode = nodes[it.toId]
            parentNode?.nodes?.add(childNode!!)
        }

        return nodes
    }
}
