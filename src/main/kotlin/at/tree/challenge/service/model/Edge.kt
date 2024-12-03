package at.tree.challenge.service.model

import at.tree.challenge.api.dto.EdgeDto

data class Edge(val fromId: Long, val toId: Long) {
    constructor(edgeDto: EdgeDto): this(edgeDto.fromId, edgeDto.toId)
}
