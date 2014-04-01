package hu.sztaki.ilab.longneck.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class MaxMatchingTest {

    
    @Test
    public void test00() {
        boolean[][] graph = new boolean[5][5];
        assertEquals(0, MaxMatching.maxMatching(graph));
    }

    @Test
    public void test01() {
        boolean[][] graph = new boolean[][] { {true, true }, {true, false } };
        assertEquals(2, MaxMatching.maxMatching(graph));
    }

    @Test
    public void test02() {
        boolean[][] graph = new boolean[5][5];
        graph[0][1]=true;
        graph[1][3]=true;
        graph[2][2]=true;
        graph[3][4]=true;
        graph[4][0]=true;
        assertEquals(5, MaxMatching.maxMatching(graph));
    }

    @Test
    public void test03() {
        boolean[][] graph = new boolean[5][5];
        graph[0][1]=true;
        graph[1][3]=true;
        graph[2][2]=true;
        graph[3][4]=true;
        graph[4][0]=true;
        graph[4][2]=true;
        graph[3][3]=true;
        graph[1][2]=true;
        graph[2][4]=true;
        assertEquals(5, MaxMatching.maxMatching(graph));
    }

    @Test
    public void test04() {
        boolean[][] graph = new boolean[5][5];
        graph[0][0]=true;
        graph[1][1]=true;
        graph[2][2]=true;
        graph[3][3]=true;
        graph[0][1]=true;
        graph[0][2]=true;
        graph[1][3]=true;
        graph[2][3]=true;
        graph[1][4]=true;
        assertEquals(4, MaxMatching.maxMatching(graph));
    }
    @Test
    public void test05() {
        boolean[][] graph = new boolean[5][5];
        graph[0][0]=true;
        graph[1][1]=true;
        graph[2][2]=true;
        graph[0][1]=true;
        graph[0][2]=true;
        graph[1][2]=true;
        graph[2][3]=true;
        graph[1][4]=true;
        graph[1][3]=true;
        assertEquals(3, MaxMatching.maxMatching(graph));
    }

}
