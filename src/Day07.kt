fun main() {

    open class Directory(open val name: String) {
        val children = mutableListOf<Directory>()
        var parentDir: Directory? = null

        override fun toString(): String {
            return "Directory(name=${name}, parentDir=${parentDir})"
        }
    }

    class File(override val name: String, val size: Int) : Directory(name) {
        override fun toString(): String {
            return "File(name=${name}, size=${size})"
        }
    }

    class FileTree {
        // maintains parent-child relation
        // used during "cd .."
        var directory: Directory = Directory("/")

        fun execCommand(cmd: String) {
            val words = cmd.split(" ")
            if (words[1] == "ls") return // ignore this, only its output is useful
            if (words[1] == "cd") {
                handleChangeDirectory(words[2])
            } else {
                // for the output of "ls" command
                val isFile = words[0].toIntOrNull() != null // file output starts with size
                if (isFile) {
                    directory.children.add(File(words[1], words[0].toInt()))
                } else if (words[0] == "dir") { // is directory
                    directory.children.add(Directory(words[1]))
                }
            }
        }

        private fun handleChangeDirectory(dir: String) {
            when (dir) {
                ".." -> directory = directory.parentDir!! // set parent directory as current dir
                "/" -> directory // do nothing
                else -> {
                    val newDir = directory.children.find { it.name == dir }!!
                    newDir.parentDir = directory // set current directory as its parent
                    directory = newDir  // navigate inside
                }
            }
        }

        fun calcSize(rootDir: Directory): Int {
            var size = 0
            if (rootDir is File) {
                size += rootDir.size
            } else {
                rootDir.children.forEach { size += calcSize(it) }
            }
            return size
        }

        fun findDirSizeLowerThan(rootDir: Directory, limit: Int): List<Int> {
            fun calcSize(rootDir: Directory, collector: MutableList<Int>): Int {
                var size = 0
                if (rootDir is File) {
                    size += rootDir.size
                } else {
                    rootDir.children.forEach {
                        size += calcSize(it, collector)
                    }
                }
                if (size < limit && rootDir !is File) {
                    collector.add(size)
                }
                return size
            }

            val collector = mutableListOf<Int>()
            calcSize(rootDir, collector)
            return collector
        }

        fun findSizesOfDirectories(rootDir: Directory): Map<String, Int> {
            fun calcSize(rootDir: Directory, collector: MutableMap<String, Int>): Int {
                var size = 0
                if (rootDir is File) {
                    size += rootDir.size
                } else {
                    rootDir.children.forEach {
                        size += calcSize(it, collector)
                    }
                }
                if (rootDir !is File) {
                    collector[rootDir.name] = size
                }
                return size
            }

            val collector = mutableMapOf<String, Int>()
            calcSize(rootDir, collector)
            return collector
        }
    }

    fun part1(input: List<String>): Int {
        val fileTree = FileTree()
        val rootDir = fileTree.directory
        input.forEach(fileTree::execCommand)
        val collector = fileTree.findDirSizeLowerThan(rootDir, 100000)
        return collector.sum()
    }

    fun part2(input: List<String>): Int {
        val fileTree = FileTree()
        val rootDir = fileTree.directory
        input.forEach(fileTree::execCommand)

        val totalSpace = 70000000
        val totalSpaceRequiredForUpdate = 30000000

        val sizeOfRootDir = fileTree.calcSize(rootDir)
        val freeSpace = totalSpace - sizeOfRootDir
        val spaceRequired = totalSpaceRequiredForUpdate - freeSpace

        val dirSizes = fileTree.findSizesOfDirectories(rootDir)
        return dirSizes.values.sorted().find { it >= spaceRequired }!!
    }

    run {
        val input = readInput("Day07_test")
        assert(part1(input) == 95437)
        assert(part2(input) == 24933642)
    }
    run {
        val input = readInput("Day07")
        println(part1(input))
        println(part2(input))
    }

}

