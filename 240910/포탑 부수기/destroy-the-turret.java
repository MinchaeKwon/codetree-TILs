import java.util.*;
import java.io.*;

public class Main {
	
	static class Node implements Comparable<Node> {
		int x;
		int y;
		int power;
		int attack;
		
		public Node(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		public Node(int x, int y, int power, int attack) {
			this.x = x;
			this.y = y;
			this.power = power;
			this.attack = attack;
		}

		@Override
		public int compareTo(Node o) {
			if (this.power == o.power) {
				if (this.attack == o.attack) {
					if ((this.x + this.y) == (o.x + o.y)) {
						return Integer.compare(o.y, this.y);
					}
					
					return Integer.compare((o.x + o.y), (this.x + this.y));
				}
				
				return Integer.compare(o.attack, this.attack);
			}
			
			return Integer.compare(this.power, o.power);
		}
	}
	
	// 우하좌상
	static int[] dx = {0, 1, 0, -1, -1, -1, 1, 1};
	static int[] dy = {1, 0, -1, 0, -1, 1, -1, 1};
	
	static int N, M, K;
	
	static int[][] map;
	static int[][] attack;
	static boolean[][] effect;

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		
		N = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		K = Integer.parseInt(st.nextToken());
		
		map = new int[N][M];
		attack = new int[N][M];

		for (int i = 0; i < N; i++) {
			st = new StringTokenizer(br.readLine());
			
			for (int j = 0; j < M; j++) {
				map[i][j] = Integer.parseInt(st.nextToken());
			}
		}
		
		for (int t = 1; t <= K; t++) {
			if (isFinish()) {
				break;
			}
			
			effect = new boolean[N][M];
			
			ArrayList<Node> list = new ArrayList<>();
			
			for (int i = 0; i < N; i++) {
				for (int j = 0; j < M; j++) {
					if (map[i][j] > 0) {
						list.add(new Node(i, j, map[i][j], attack[i][j]));
					}
				}
			}
			
			Collections.sort(list);
			
			// 공격력이 가장 낮은 포탑 구하기
			Node start = list.get(0);
			
			attack[start.x][start.y] = t;
			
			start.power += (N + M);
			map[start.x][start.y] = start.power;
			
			effect[start.x][start.y] = true;
			
			// 공격력이 가장 높은 포탑 구하기
			Node target = list.get(list.size() - 1);
			
			if (!laser(start, target)) {
				bomb(start, target);
			}
			
			add();
		}
		
		int answer = 0;
		
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < M; j++) {
				answer = Math.max(answer, map[i][j]);
			}
		}
		
		System.out.println(answer);
	}
	
	// 포탑이 남아있는지 확인
	private static boolean isFinish() {
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < M; j++) {
				if (map[i][j] > 0) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	// 레이저 공격
	private static boolean laser(Node start, Node end) {
		Queue<Node> q = new LinkedList<>();
		boolean[][] visited = new boolean[N][M];
		
		q.add(start);
		visited[start.x][start.y] = true;
		
		Node[][] route = new Node[N][M];
		boolean canGo = false;
		
		while (!q.isEmpty()) {
			Node cur = q.poll();
			
			if (cur.x == end.x && cur.y == end.y) {
				canGo = true;
				break;
			}
			
			for (int i = 0; i < 4; i++) {
				int nx = (N + cur.x + dx[i]) % N;
				int ny = (M + cur.y + dy[i]) % M;
				
				if (!isRange(nx, ny) || visited[nx][ny] || map[nx][ny] <= 0) {
					continue;
				}
				
				q.add(new Node(nx, ny));
				visited[nx][ny] = true;
				
				route[nx][ny] = cur;
			}
		}
		
		if (!canGo) {
			return false;
		}
		
		// 역추적
		map[end.x][end.y] -= start.power;
		effect[end.x][end.y] = true;
		
		Node cur = route[end.x][end.y];
		
		int x = cur.x;
		int y = cur.y;
		
		while (!(x == start.x && y == start.y)) {
			map[x][y] -= start.power / 2;
			effect[x][y] = true;
			
			cur = route[x][y];
			x = cur.x;
			y = cur.y;
		}
		
		return true;
	}
	
	// 포탄 공격
	private static void bomb(Node start, Node end) {
		map[end.x][end.y] -= start.power;
		effect[end.x][end.y] = true;
		
		for (int i = 0; i < 8; i++) {
			int nx = (N + end.x + dx[i]) % N;
			int ny = (M + end.y + dy[i]) % M;
			
			if (map[nx][ny] <= 0 || (nx == start.x && ny == start.y)) {
				continue;
			}
			
			map[nx][ny] -= start.power / 2;
			effect[nx][ny] = true;
		}
	}
	
	// 포탑 정비
	private static void add() {
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < M; j++) {
				if (!effect[i][j] && map[i][j] > 0) {
					map[i][j]++;
				}
			}
		}
	}
	
	private static boolean isRange(int x, int y) {
		return x >= 0 && x < N && y >= 0 && y < M;
	}

}