package at.tree.challenge.api

import at.tree.challenge.api.dto.EdgeDto
import at.tree.challenge.api.dto.NodeDto
import at.tree.challenge.service.TreeService
import at.tree.challenge.service.model.Edge
import jakarta.validation.constraints.Max
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("tree")
class TreeController @Autowired constructor(private val treeService: TreeService) {

    @GetMapping("{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getTree(
        @PathVariable("id") edgeId: Long,
        @RequestParam("depth") @Validated @Max(500) depth: Long = 500
    ): NodeDto {
        return NodeDto(treeService.getTree(edgeId, depth))
    }

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createEdge(@Validated @RequestBody edgeDto: EdgeDto) = treeService.createEdge(Edge(edgeDto))

    @DeleteMapping("{fromId}/{toId}")
    fun deleteEdge(@PathVariable("fromId") fromId: Long, @PathVariable("toId") toId: Long) =
        treeService.deleteEdge(Edge(fromId, toId))
}
