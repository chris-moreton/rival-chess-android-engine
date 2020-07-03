package com.netsensia.rivalchess.engine

import com.netsensia.rivalchess.config.MATE_SCORE_START
import com.netsensia.rivalchess.config.MAX_SEARCH_DEPTH
import com.netsensia.rivalchess.config.MAX_SEARCH_MILLIS
import com.netsensia.rivalchess.engine.search.Search
import com.netsensia.rivalchess.exception.IllegalFenException
import com.netsensia.rivalchess.model.Board
import com.netsensia.rivalchess.util.getSimpleAlgebraicMoveFromCompactMove
import org.awaitility.Awaitility
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test
import java.util.concurrent.TimeUnit

class SearchTest {
    companion object {
        private const val RECALCULATE = false
        private const val CHECKMATE = 9999
        private const val MATE_2 = 9997
        private const val MATE_3 = 9995
        private const val MATE_4 = 9993
        private const val MATE_5 = 9991
        private const val MATE_6 = 9989
        private const val MATE_7 = 9987
        private const val MATE_8 = 9985
        private const val MATE_9 = 9983
    }

    @Test
    @Throws(IllegalFenException::class, InterruptedException::class)
    fun testQueenVKingEndGame() {
        solveForMate("8/8/8/8/1q6/8/4k3/2K5 b - - 0 1", "e2d3", MATE_2)
        solveForMate("8/8/8/8/1q6/3k4/8/3K4 b - - 0 1", "b4b1", CHECKMATE)
        solveForMate("8/8/8/8/1q6/4k3/2K5/8 b - - 0 1", "e3e2", MATE_3)
        solveForMate("8/8/8/8/1q6/8/2K1k3/8 b - - 0 1", "e2e3", MATE_3)
        solveForMate("8/8/8/8/5q2/8/1K2k3/8 b - - 0 1", "f4b4", MATE_4)
    }

    @Throws(IllegalFenException::class, InterruptedException::class)
    @Test
    fun testQueenVKingEndGameDodgyPositions() {
        solveForMate("8/8/8/8/2K5/7k/1q6/8 b - - 0 1", "b2c2", MATE_8, false) // b2b6, b2e5, h3g4, b2a2
        solveForMate("8/8/8/8/5q2/K7/3k4/8 b - - 0 1", "d2c2", MATE_2)
        solveForMate("8/8/8/8/5q2/1K6/4k3/8 b - - 0 1", "e2d3", MATE_4, false) // e2d2
        solveForMate("8/8/8/8/5q2/2K5/5k2/8 b - - 0 1", "f2e2", MATE_5, false) // f4a4
        solveForMate("8/8/8/4q3/8/3K4/5k2/8 b - - 0 1", "e5c5", MATE_4, false) // e5f4, e5c5
        solveForMate("8/8/1K6/4q3/8/7k/8/8 b - - 0 1", "e5d6", MATE_7, false) // h3g4, e5d5
        solveForMate("8/8/8/4q3/2K5/8/6k1/8 b - - 0 1", "e5a5", MATE_6, false) // also e5a5
        solveForMate("8/8/8/2K5/8/7k/1q6/8 b - - 0 1", "b2c3", MATE_8, false) // also h3g4, b2b3, h3g3, b2e5
    }

    @Throws(IllegalFenException::class, InterruptedException::class)
    @Test
    fun testMateIn8ToCheckMate_1() {
        solveForMate("8/8/8/8/1q6/3k4/8/3K4 b - - 0 1", "b4b1", CHECKMATE, false)
        solveForMate("8/8/8/8/1q6/3k4/8/2K5 w - - 0 1", "c1d1", mated(CHECKMATE), false)
        solveForMate("8/8/8/8/1q6/4k3/8/2K5 b - - 0 1", "e3d3", MATE_2, false)
        solveForMate("8/8/8/8/1q6/4k3/2K5/8 w - - 0 1", "c2c1", mated(CHECKMATE), false)
        solveForMate("8/8/8/1q6/8/4k3/2K5/8 b - - 0 1", "b5b4", MATE_3, false)
        solveForMate("8/8/8/1q6/8/2K1k3/8/8 w - - 0 1", "c3c2", mated(MATE_2), false)
        solveForMate("8/8/8/1q6/8/2K2k2/8/8 b - - 0 1", "f3e3", MATE_4, false)
        solveForMate("8/8/8/1q6/8/5k2/1K6/8 w - - 0 1", "b2c3", mated(MATE_3), false)
        solveForMate("8/8/8/2q5/8/5k2/1K6/8 b - - 0 1", "c5b5", MATE_5, false)
    }

    @Throws(IllegalFenException::class, InterruptedException::class)
    @Test
    fun testMateIn8ToCheckMate_2() {
        solveForMate("8/8/8/2q5/8/2K2k2/8/8 w - - 0 1", "c3b2", mated(MATE_4), false)
        solveForMate("8/8/8/3q4/8/2K2k2/8/8 b - - 0 1", "d5c5", MATE_6, false)
        solveForMate("8/8/8/3q4/1K6/5k2/8/8 w - - 0 1", "b4c3", mated(MATE_4), false)
        solveForMate("8/8/8/3q4/1K4k1/8/8/8 b - - 0 1", "g4f3", MATE_6, false)
        solveForMate("8/8/8/3q4/K5k1/8/8/8 w - - 0 1", "a4a3", mated(MATE_5), false)
        solveForMate("8/8/8/3q4/K7/7k/8/8 b - - 0 1", "d5c6", MATE_8, false)
        solveForMate("8/8/8/1K1q4/8/7k/8/8 w - - 0 1", "b5a4", mated(MATE_6), false)
        solveForMate("8/8/3q4/1K6/8/7k/8/8 b - - 0 1", "d6d5", MATE_8, false)
        solveForMate("8/8/3q4/8/1K6/7k/8/8 w - - 0 1", "b4b5", mated(MATE_7), false)
        solveForMate("8/8/8/4q3/1K6/7k/8/8 b - - 0 1", "h3g2", MATE_7, false)
    }

    fun mated(score: Int) = -(score-1)

    @Test
    @Throws(IllegalFenException::class, InterruptedException::class)
    fun testBestMoves() {
        assertBestMove("8/4k3/8/8/2p2P2/8/2P5/5K2 b - - 0 1", "e7f6", -174);
        assertBestMove("k7/5RP1/1P6/1K6/6r1/8/8/8 b - -", "g4g1", -1415);
        assertBestMove("5k2/5p1p/p3B1p1/P5P1/3K1P1P/8/8/8 b - -", "f7e6", -204);
        assertBestMove("8/3K4/2p5/p2b2r1/5k2/8/8/1q6 b - - 1 67", "b1b7", 9995);
        assertBestMove("n1n5/PPPk4/8/8/8/8/4Kppp/5N1N b - - 0 1", "g2h1q", 1261);
        assertBestMove("8/7p/p5pb/4k3/P1pPn3/8/P5PP/1rB2RK1 b - d3 0 28", "e5d4", 130);
        assertBestMove("rnbqkb1r/ppppp1pp/7n/4Pp2/8/8/PPPP1PPP/RNBQKBNR w KQkq f6 0 3", "d2d4", 77);
        assertBestMove("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - -", "e2e3", 8);
        assertBestMove("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq -", "e2a6", 31);
    }

    @Test
    @Throws(IllegalFenException::class, InterruptedException::class)
    fun testNodeCount() {
        assertNodeCount("5k2/5p1p/p3B1p1/P5P1/3K1P1P/8/8/8 b - -", 4358);
        assertNodeCount("8/3K4/2p5/p2b2r1/5k2/8/8/1q6 b - - 1 67", 2637);
        assertNodeCount("8/7p/p5pb/4k3/P1pPn3/8/P5PP/1rB2RK1 b - d3 0 28", 15743);
        assertNodeCount("rnbqkb1r/ppppp1pp/7n/4Pp2/8/8/PPPP1PPP/RNBQKBNR w KQkq f6 0 3", 90135);
        assertNodeCount("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - -", 83386);
        assertNodeCount("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq -", 177470);
        assertNodeCount("6k1/p5p1/5p2/2P2Q2/3pN2p/3PbK1P/7P/6q1 b - -", 95724);
        assertNodeCount("8/k1b5/P4p2/1Pp2p1p/K1P2P1P/8/3B4/8 w - -", 4148);
        assertNodeCount("3r2k1/ppp2ppp/6q1/b4n2/3nQB2/2p5/P4PPP/RN3RK1 b - -", 204013);
        assertNodeCount("4r3/1Q1qk2p/p4pp1/3Pb3/P7/6PP/5P2/4R1K1 w - -", 142228);
        assertNodeCount("k7/5RP1/1P6/1K6/6r1/8/8/8 b - -", 33451);
        assertNodeCount("n1n5/PPPk4/8/8/8/8/4Kppp/5N1N b - - 0 1", 43530);
    }

    @Throws(IllegalFenException::class, InterruptedException::class)
    private fun assertBestMove(fen: String, expectedMove: String, expectedScore: Int, searchDepth: Int = MAX_SEARCH_DEPTH, searchMillis: Int = 5000, searchNodes: Int = 500000) {
        val search = Search()
        val board = Board.fromFen(fen)
        Thread(search).start()
        search.setBoard(board)
        search.setHashSizeMB(8)
        search.setSearchDepth(searchDepth)
        search.setMillisToThink(searchMillis)
        search.setNodesToSearch(searchNodes)
        search.startSearch()
        TimeUnit.MILLISECONDS.sleep(100)
        Awaitility.await().atMost(searchMillis.toLong() + 1000, TimeUnit.MILLISECONDS).until { !search.isSearching }

        if (RECALCULATE) {
            println("assertBestMove(\"" + fen + "\", \"" + getSimpleAlgebraicMoveFromCompactMove(search.currentMove) + "\", " + search.currentScore + ");")
        } else {
            Assert.assertEquals(expectedMove, getSimpleAlgebraicMoveFromCompactMove(search.currentMove))
            Assert.assertEquals(expectedScore.toLong(), search.currentScore.toLong())
        }

    }

    @Throws(IllegalFenException::class, InterruptedException::class)
    private fun solveForMate(fen: String, expectedMove: String, expectedScore: Int, tolerateScore: Boolean = false) {
        solveForMate(fen, listOf(expectedMove), expectedScore, tolerateScore)
    }

    @Throws(IllegalFenException::class, InterruptedException::class)
    private fun solveForMate(fen: String, expectedMoves: List<String>, expectedScore: Int, tolerateScore: Boolean = false) {
        val search = Search()
        val board = Board.fromFen(fen)
        Thread(search).start()
        search.setBoard(board)
        search.setHashSizeMB(8)
        search.setSearchDepth(Int.MAX_VALUE)
        search.setMillisToThink(MAX_SEARCH_MILLIS)
        search.setNodesToSearch(10000000)
        search.startSearch()
        TimeUnit.MILLISECONDS.sleep(100)
        Awaitility.await().atMost(310000, TimeUnit.MILLISECONDS).until { !search.isSearching }
        println("path " + search.currentPath.toString().trim { it <= ' ' } + " | nodes " + search.nodes + " | score " + search.currentScoreHuman + " (" + search.currentScore + ")")
        Assert.assertTrue(expectedMoves.contains(getSimpleAlgebraicMoveFromCompactMove(search.currentMove)))
        val score  = search.currentScore.toLong()
        if (tolerateScore) {
            Assert.assertTrue(score <= MATE_SCORE_START || score >= MATE_SCORE_START)
        } else {
            Assert.assertEquals(expectedScore.toLong(), score)
        }
    }

    @Throws(IllegalFenException::class, InterruptedException::class)
    private fun assertNodeCount(fen: String, expectedNodes: Int) {
        val board = Board.fromFen(fen)
        val search = Search()
        Thread(search).start()
        search.setBoard(board)
        search.setSearchDepth(6)
        search.setMillisToThink(MAX_SEARCH_MILLIS)
        search.startSearch()
        TimeUnit.SECONDS.sleep(1)
        Awaitility.await().atMost(60, TimeUnit.SECONDS).until { !search.isSearching }
        if (RECALCULATE) {
            println("assertNodeCount(\"" + fen + "\", " + search.nodes + ");")
        } else {
            Assert.assertEquals(expectedNodes, search.nodes)
        }
    }

}