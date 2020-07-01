package com.netsensia.rivalchess.engine;

import com.netsensia.rivalchess.engine.search.Search;
import com.netsensia.rivalchess.exception.IllegalFenException;
import com.netsensia.rivalchess.model.Board;

import static com.netsensia.rivalchess.config.LimitKt.MAX_SEARCH_DEPTH;
import static com.netsensia.rivalchess.config.LimitKt.MAX_SEARCH_MILLIS;
import static com.netsensia.rivalchess.util.ChessBoardConversionKt.getSimpleAlgebraicMoveFromCompactMove;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SearchTest {

    private static Boolean RECALCULATE = false;

    @Test
    public void testQueenvKingEndGame() throws IllegalFenException, InterruptedException {
        assertBestMove("8/8/8/8/5q2/2K5/5k2/8 b - - 0 1", "f2e3", 9991, MAX_SEARCH_DEPTH, 2000); // mate in 5
        assertBestMove("8/8/8/8/1q6/3k4/8/3K4 b - - 0 1", "b4b1", 9999, MAX_SEARCH_DEPTH, 2000); // checkmate
        assertBestMove("8/8/8/8/1q6/8/4k3/2K5 b - - 0 1", "e2d3", 9997, MAX_SEARCH_DEPTH, 2000); // mate in 2
        assertBestMove("8/8/8/8/1q6/4k3/2K5/8 b - - 0 1", "e3e2", 9995, MAX_SEARCH_DEPTH, 2000); // mate in 3
        assertBestMove("8/8/8/8/1q6/8/2K1k3/8 b - - 0 1", "e2e3", 9995, MAX_SEARCH_DEPTH, 2000); // mate in 3
        assertBestMove("8/8/8/8/5q2/8/1K2k3/8 b - - 0 1", "f4b4", 9993, MAX_SEARCH_DEPTH, 2000); // mate in 4
        assertBestMove("8/8/8/4q3/8/3K4/5k2/8 b - - 0 1", "e5f4", 9989, MAX_SEARCH_DEPTH, 2000); // mate in 6
        assertBestMove("8/8/1K6/4q3/8/7k/8/8 w - - 0 1", "h3g4", 9987, MAX_SEARCH_DEPTH, 2000); // mate in 7
        assertBestMove("8/8/8/4q3/2K5/8/6k1/8 b - - 0 1", "g2f2", 9987, MAX_SEARCH_DEPTH, 2000); // mate in 7
        assertBestMove("8/8/8/4q3/1K6/7k/8/8 b - - 0 1", "h3g2", 9985, MAX_SEARCH_DEPTH, 2000); // mate in 8
        assertBestMove("8/8/8/8/2K5/7k/1q6/8 b - - 0 1", "b2e5", 9983, MAX_SEARCH_DEPTH, 2000); // mate in 9
        assertBestMove("8/8/8/2K5/8/7k/1q6/8 b - - 0 1", "h3g4", 9981, MAX_SEARCH_DEPTH, 2000); // mate in 9
    }

    @Test
    public void testBestMoves() throws IllegalFenException, InterruptedException {
        assertBestMove("8/4k3/8/8/2p2P2/8/2P5/5K2 b - - 0 1", "e7e6", -1469, MAX_SEARCH_DEPTH - 2, 2000);
        assertBestMove("k7/5RP1/1P6/1K6/6r1/8/8/8 b - -", "g4g2", -1459, 4, MAX_SEARCH_MILLIS);
        assertBestMove("5k2/5p1p/p3B1p1/P5P1/3K1P1P/8/8/8 b - -", "f7e6", -86, 4, MAX_SEARCH_MILLIS);
        assertBestMove("8/3K4/2p5/p2b2r1/5k2/8/8/1q6 b - - 1 67", "b1b7", 9996, 4, MAX_SEARCH_MILLIS);
        assertBestMove("n1n5/PPPk4/8/8/8/8/4Kppp/5N1N b - - 0 1", "g2h1q", 1121, 4, MAX_SEARCH_MILLIS);
        assertBestMove("8/7p/p5pb/4k3/P1pPn3/8/P5PP/1rB2RK1 b - d3 0 28", "e5d4", 185, 4, MAX_SEARCH_MILLIS);
        assertBestMove("rnbqkb1r/ppppp1pp/7n/4Pp2/8/8/PPPP1PPP/RNBQKBNR w KQkq f6 0 3", "b1c3", 55, 4, MAX_SEARCH_MILLIS);
        assertBestMove("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - -", "b4f4", 80, 4, MAX_SEARCH_MILLIS);
        assertBestMove("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq -", "e2a6", 24, 4, MAX_SEARCH_MILLIS);
    }

    @Test
    public void testNodeCount() throws IllegalFenException, InterruptedException {
        assertNodeCount("5k2/5p1p/p3B1p1/P5P1/3K1P1P/8/8/8 b - -", 3734);
        assertNodeCount("8/3K4/2p5/p2b2r1/5k2/8/8/1q6 b - - 1 67", 2012);
        assertNodeCount("8/7p/p5pb/4k3/P1pPn3/8/P5PP/1rB2RK1 b - d3 0 28", 11588);
        assertNodeCount("rnbqkb1r/ppppp1pp/7n/4Pp2/8/8/PPPP1PPP/RNBQKBNR w KQkq f6 0 3", 89935);
        assertNodeCount("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - -", 38413);
        assertNodeCount("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq -", 169426);
        assertNodeCount("6k1/p5p1/5p2/2P2Q2/3pN2p/3PbK1P/7P/6q1 b - -", 100001);
        assertNodeCount("8/k1b5/P4p2/1Pp2p1p/K1P2P1P/8/3B4/8 w - -", 5721);
        assertNodeCount("3r2k1/ppp2ppp/6q1/b4n2/3nQB2/2p5/P4PPP/RN3RK1 b - -", 197479);
        assertNodeCount("4r3/1Q1qk2p/p4pp1/3Pb3/P7/6PP/5P2/4R1K1 w - -", 157701);
        assertNodeCount("k7/5RP1/1P6/1K6/6r1/8/8/8 b - -", 32212);
        assertNodeCount("n1n5/PPPk4/8/8/8/8/4Kppp/5N1N b - - 0 1", 28574);
    }

    private void assertBestMove(String fen, String expectedMove, int expectedScore, int searchDepth, int searchMillis) throws IllegalFenException, InterruptedException {

        Search search = new Search();

        Board board = Board.fromFen(fen);

        new Thread(search).start();

        search.setBoard(board);
        search.setSearchDepth(searchDepth);
        search.setMillisToThink(searchMillis);
        search.setNodesToSearch(5000000);
        search.startSearch();

        MILLISECONDS.sleep(1000);

        await().atMost(10, SECONDS).until(() -> !search.isSearching());

        assertEquals(expectedMove, getSimpleAlgebraicMoveFromCompactMove(search.getCurrentMove()));
        assertEquals(expectedScore, search.getCurrentScore());

    }

    private void assertNodeCount(String fen, long expectedNodes) throws IllegalFenException, InterruptedException {

        Board board = Board.fromFen(fen);

        Search search = new Search();

        new Thread(search).start();

        search.setBoard(board);
        search.setSearchDepth(6);
        search.setMillisToThink(MAX_SEARCH_MILLIS);
        search.startSearch();

        SECONDS.sleep(1);

        await().atMost(60, SECONDS).until(() -> !search.isSearching());

        if (RECALCULATE) {
            System.out.println("assertNodeCount(\"" + fen + "\", " + search.getNodes() + ");");
        } else {
            assertEquals(expectedNodes, search.getNodes());
        }
    }


}
