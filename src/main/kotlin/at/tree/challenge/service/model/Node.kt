package at.tree.challenge.service.model

data class Node(val value: Long, val nodes: ArrayList<Node> = ArrayList())
