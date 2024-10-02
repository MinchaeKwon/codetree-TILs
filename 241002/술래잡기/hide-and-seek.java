import java.util.*;
import java.io.*;

public class Main {
	
	static class Node {
		int x;
		int y;
		int d;
		
		public Node(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		public Node(int x, int y, int d) {
			this.x = x;
			this.y = y;
			this.d = d;
		}
	}
	
	// 상우하좌
	static int[] dx = {-1, 0, 1, 0};
	static int[] dy = {0, 1, 0, -1};
	
	static int[] dc = {1, 1, 2, 2};
	
	static int n, m, h, k;
	
	static int sx, sy;
	
	static ArrayList<Integer>[][] map;
	static ArrayList<Integer>[][] newMap;
	
	static boolean[][] tree;
	
	static int[][] dirMap;
	static int[][] revDirMap;
	
	static boolean reverse;

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());

		n = Integer.parseInt(st.nextToken());
		m = Integer.parseInt(st.nextToken());
		h = Integer.parseInt(st.nextToken());
		k = Integer.parseInt(st.nextToken());
		
		map = new ArrayList[n][n];
		newMap = new ArrayList[n][n];
		tree = new boolean[n][n];
		
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				map[i][j] = new ArrayList<>();
			}
		}
		
		for (int i = 0; i < m; i++) {
			st = new StringTokenizer(br.readLine());
			
			int x = Integer.parseInt(st.nextToken()) - 1;
			int y = Integer.parseInt(st.nextToken()) - 1;
			int d = Integer.parseInt(st.nextToken());
			
			map[x][y].add(d);
		}
		
		for (int i = 0; i < h; i++) {
			st = new StringTokenizer(br.readLine());
			
			int x = Integer.parseInt(st.nextToken()) - 1;
			int y = Integer.parseInt(st.nextToken()) - 1;
			
			tree[x][y] = true;
		}
		
		sx = n / 2;
		sy = n / 2;
		
		setDirMap();
		
		int answer = 0;
		
		for (int turn = 1; turn <= k; turn++) {
			moveAllHide();
			
			moveSeek();
			answer += turn * catchHide();
		}
		
		System.out.println(answer);
	}
	
	// 술래가 이동하는 방향 저장
	private static void setDirMap() {
		dirMap = new int[n][n];
		revDirMap = new int[n][n];
		
		int x = sx;
		int y = sy;
		
		while (true) {
			for (int d = 0; d < 4; d++) {
				for (int c = 0; c < dc[d]; c++) {
					dirMap[x][y] = d;
					
					x += dx[d];
					y += dy[d];
					
					revDirMap[x][y] = (d + 2) % 4;
					
					if (x == 0 && y == 0) {
						return;
					}
				}
			}
			
			for (int i = 0; i < 4; i++) {
				dc[i] += 2;
			}
		}
	}
	
	// 모든 도망자 이동
	private static void moveAllHide() {
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				newMap[i][j] = new ArrayList<>();
			}
		}
		
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (map[i][j].isEmpty()) {
					continue;
				}
				
				int dist = Math.abs(sx - i) + Math.abs(sy - j);
				
				if (dist <= 3) {
					// 거리가 3이하인 경우에만 움직임
					for (int d : map[i][j]) {
						moveHide(i, j, d);
					}
				} else {
					// 거리가 3을 초과하는 경우 움직이지 않음
					newMap[i][j].addAll(map[i][j]);
				}
			}
		}
		
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				map[i][j] = new ArrayList<>(newMap[i][j]);
			}
		}
	}
	
	// 이동 가능한 도망자 이동
	private static void moveHide(int x, int y, int d) {
		int nx = x + dx[d];
		int ny = y + dy[d];
		
		if (!isRange(nx, ny)) {
			d = (d + 2) % 4;
			
			nx = x + dx[d];
			ny = y + dy[d];
		}
		
		if (!(nx == sx && ny == sy)) {
			x = nx;
			y = ny;
		}
		
		newMap[x][y].add(d);
	}
	
	private static void moveSeek() {
		int dir = getSeekDir();
		
		sx += dx[dir];
		sy += dy[dir];
		
		if (!reverse && sx == 0 && sy == 0) {
			// 방향 틀어줌
			reverse = true;
		} else if (reverse && sx == n / 2 && sy == n / 2) {
			reverse = false;
		}
	}
	
	// 도망자 잡기
	private static int catchHide() {
		int dir = getSeekDir(); // 이동시킨 후에 다시 방향을 구해야 현재 술래가 바라보는 방향을 얻을 수 있음
		
		int result = 0;
		
		int nx = sx;
		int ny = sy;
		
		for (int cnt = 0; cnt < 3; cnt++) {
			if (isRange(nx, ny) && !tree[nx][ny]) {
				result += map[nx][ny].size();
				map[nx][ny].clear();
			}
			
			nx += dx[dir];
			ny += dy[dir];
		}
		
		return result;
	}
	
	private static int getSeekDir() {
		return reverse ? revDirMap[sx][sy] : dirMap[sx][sy];
	}

	private static boolean isRange(int x, int y) {
		return x >= 0 && x < n && y >= 0 && y < n;
	}
}