package at.tree.challenge.api.dto

import at.tree.challenge.service.model.Node
import org.jetbrains.annotations.NotNull

data class NodeDto(@NotNull val value: Long, @NotNull val nodes: List<NodeDto> = ArrayList()) {
    constructor(node: Node): this(node.value, node.nodes.map { NodeDto(it) })
}
