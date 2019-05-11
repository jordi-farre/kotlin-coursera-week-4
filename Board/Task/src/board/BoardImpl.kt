package board

import board.Direction.*

fun createSquareBoard(width: Int): SquareBoard = SquareBoardImpl(width)
fun <T> createGameBoard(width: Int): GameBoard<T> = GameBoardImpl(width)

open class SquareBoardImpl(final override val width: Int): SquareBoard {

    private val cells: List<Cell>

    init {
        cells = (1..width).map { rowNumber ->
            (1..width)
                .map { Cell(1, it) }
                .map { cell -> Cell(rowNumber * cell.i, cell.j) } }
                .flatten()
    }

    override fun getCellOrNull(i: Int, j: Int): Cell? = cells.firstOrNull { it == Cell(i, j) }


    override fun getCell(i: Int, j: Int): Cell = cells.first { it == Cell(i, j) }

    override fun getAllCells(): Collection<Cell> = cells

    override fun getRow(i: Int, jRange: IntProgression): List<Cell> {
        val rowCells = cells.filter { it.i == i && it.j in jRange }.sortedBy { it.j }
        return if (jRange.step < 0) rowCells.reversed() else rowCells
    }

    override fun getColumn(iRange: IntProgression, j: Int): List<Cell> {
        val columnCells = cells.filter { it.i in iRange && it.j == j }.sortedBy { it.i }
        return if (iRange.step < 0) columnCells.reversed() else columnCells
    }

    override fun Cell.getNeighbour(direction: Direction): Cell? {
        return when(direction) {
            UP -> cells.firstOrNull { it == Cell(this.i - 1, this.j) }
            DOWN -> cells.firstOrNull { it == Cell(this.i + 1, this.j) }
            RIGHT -> cells.firstOrNull { it == Cell(this.i, this.j + 1) }
            LEFT -> cells.firstOrNull { it == Cell(this.i, this.j - 1) }
        }
    }

}

class GameBoardImpl<T>(width: Int) : SquareBoardImpl(width), GameBoard<T> {

    private val db: MutableMap<Cell, T?> = getAllCells().map { it to null }.toMap().toMutableMap()

    override fun get(cell: Cell): T? = db[cell]

    override fun set(cell: Cell, value: T?) {
         db[cell] = value
    }

    override fun filter(predicate: (T?) -> Boolean): Collection<Cell> = db.filterValues(predicate).keys

    override fun find(predicate: (T?) -> Boolean): Cell? = db.filterValues(predicate).keys.firstOrNull()

    override fun any(predicate: (T?) -> Boolean): Boolean = db.any { predicate(it.value) }

    override fun all(predicate: (T?) -> Boolean): Boolean = db.all { predicate(it.value) }
}

