package day7

interface FileNode {
    val name: String
    val parent: Directory?

    fun getSize(): Int
}

class Directory(
        override val name: String,
        override val parent: Directory?,
) : FileNode {
    private val children = mutableMapOf<String, FileNode>()

    override fun getSize(): Int {
        return children.values.sumBy { it.getSize() }
    }

    fun addChild(child: FileNode) {
        if (children.containsKey(child.name)) {
            throw IllegalArgumentException("Can't have two children with the same name")
        }
        children[child.name] = child
    }

    fun getChild(name: String): Directory {
        return children[name] as Directory
    }

    fun listAllSubDirs(): List<Directory> {
        val immediateSubDirs = children.values.mapNotNull { it as? Directory }
        val recursiveSubDirs = immediateSubDirs.flatMap { it.listAllSubDirs() }

        return immediateSubDirs + recursiveSubDirs
    }
}

class File(
        override val name: String,
        override val parent: Directory?,
        private val size: Int,
) : FileNode {
    override fun getSize(): Int {
        return size
    }
}

fun simulateTerminal(input: List<String>): Directory {
    val rootNode = Directory("/", null)

    var currentDirectory: Directory = rootNode
    var i = 0
    while (i < input.size) {
        if (input[i].startsWith("$ cd")) {
            val name = input[i].removePrefix("$ cd ")
            currentDirectory = when (name) {
                "/" -> rootNode
                ".." -> currentDirectory.parent!!
                else -> currentDirectory.getChild(name)
            }
            i++
        } else if (input[i].startsWith("$ ls")) {
            i++ // Go past the `ls` command

            // Start making files or directories until we hit the last line or another command
            while (i < input.size && !input[i].startsWith("$")) {
                val parts = input[i].split(" ")
                val typeOrSize = parts[0]
                val name = parts[1]

                val newNode = if (typeOrSize == "dir") {
                    Directory(name, currentDirectory)
                } else {
                    File(name, currentDirectory, typeOrSize.toInt())
                }
                currentDirectory.addChild(newNode)

                i++
            }
        }
    }

    return rootNode
}

fun solvePuzzle1(input: List<String>) {
    val fileTree = simulateTerminal(input)

    val allDirs = listOf(fileTree) + fileTree.listAllSubDirs()

    val result = allDirs.asSequence()
            .map { it.getSize() }
            .filter { it <= 100_000 }
            .sum()

    println(result)
}

fun solvePuzzle2(input: List<String>) {
    val fileTree = simulateTerminal(input)

    val totalSpace = 70_000_000
    val spaceRequired = 30_000_000

    val freeSpace = totalSpace - fileTree.getSize()
    val needToFree = spaceRequired - freeSpace

    val allDirs = listOf(fileTree) + fileTree.listAllSubDirs()

    val result = allDirs.asSequence()
            .map { it.getSize() }
            .filter { it >= needToFree }
            .min()

    println(result)
}
