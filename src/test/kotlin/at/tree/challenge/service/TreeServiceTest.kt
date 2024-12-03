package at.tree.challenge.service

import at.tree.challenge.exception.EdgeExistsException
import at.tree.challenge.exception.LoopException
import at.tree.challenge.persistence.TreeRepository
import at.tree.challenge.service.model.Edge
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.Test
import kotlin.test.assertFailsWith

// Remark: These tests are non-exhaustive
class TreeServiceTest() {
    private val treeRepository: TreeRepository = mockk()

    private val treeService: TreeService = TreeService(treeRepository)

    @Test
    fun givenEdge_createEdge_correctlyCreatesEdge() {
        // given
        val edge = Edge(1, 2)
        every {treeRepository.getEdge(edge)} returns null
        every {treeRepository.getEdgesByFromId(2)} returns emptyList()
        every {treeRepository.createEdge(edge)} returns Unit

        // when
        treeService.createEdge(edge)

        // then
        verify { treeRepository.createEdge(edge) }
    }

    @Test
    fun givenDuplicateEdge_createEdge_throws() {
        // given
        val edge = Edge(1, 2)
        every {treeRepository.getEdge(edge)} returns edge

        // when / then
        assertFailsWith<EdgeExistsException> { treeService.createEdge(edge) }
    }

    @Test
    fun givenLoopingEdge_createEdge_throws() {
        // given
        val edge = Edge(1, 2)
        every {treeRepository.getEdge(edge)} returns null
        every {treeRepository.getEdgesByFromId(2)} returns listOf(Edge(2, 1))

        // when / then
        assertFailsWith<LoopException> { treeService.createEdge(edge) }
    }
}
