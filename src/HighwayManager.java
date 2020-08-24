import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Scanner;

public class HighwayManager {

	private String[] highway_name = null;
	
	private String[][] node_name = null;
	private double[][] node_distance = null;
	// node_name.length ==> 전국 고속도로 개수, node_name[i].length ==> 각 고속도로별 노드 개수

	private ArrayList<String> junction_name = null; // JC이름 저장. ==> 83개
	private int[][] junction_index = null;          // 고속도로에서 JC인덱스만 저장.
	private double[][] junction_distance = null;    // dajikstra를 위한 JC거리 저장. 83*83
	
	private String start = null;
	private int JC_start;
	private String destination = null;
	private int JC_destination;
	private boolean jc_exist = false;
	
	Scanner scan = new Scanner(System.in);

	public HighwayManager() {
		Saving_highway highway_info = new Saving_highway();

		highway_name = highway_info.getHighwayName();
		
		node_name = highway_info.getNodeName();
		node_distance = highway_info.getNodeDistance();

		junction_name = highway_info.getJunctionName();
		junction_index = highway_info.getJunctionIndex();
		junction_distance = highway_info.getJunctionDistance();
	}

	public void menu() {
		
		while(true) {
			System.out.println("1) 최소경로 찾기  2) 종료");
			int menu = -1;
			
			while(true) {
				System.out.print("메뉴를 선택하세요 ==> ");
				menu = scan.nextInt();
				System.out.println("===============================");
					
				if(menu == 1 || menu == 2) {
					break;
				}
				else {
					System.out.println("1 또는 2를 입력하세요 : ");
				}
			}
			
			switch(menu) {
			case 1 :
				menu1();
				break;
			case 2 :
				System.out.println("안녕히계세요.");
				System.exit(0);
			}
			
			System.out.println();
			System.out.print("메인메뉴로 나가시겠습니까? (y / n) : ");
			char ch = scan.next().charAt(0);
			System.out.println("===============================");
			
			if(ch == 'y') {
				
			}
			else {
				System.out.println("한 학기동안 전산수학 가르쳐주셔서 감사합니다.");
				break;
			}
		}
	}
	
	public void menu1() {
		/*for(int i = 0; i < highway_name.length; i++) {
			System.out.println((i) + ") " + highway_name[i]);
		}*/
		
		while(true) {
			System.out.print("출발지를 입력하세요 : ");
			start = scan.next();
			
			if(start.equals("시점") || start.equals("종점")) {
				System.out.println("다시 골라주세요.");
			}
			else {
				break;
			}
			
		}
		
		// System.out.println(start);
		while(true) {
			System.out.print("도착지를 입력하세요 : ");
			destination = scan.next();
			
			if(destination.equals("시점") || destination.equals("종점")) {
				System.out.println("다시 골라주세요.");
			}
			else {
				break;
			}	
		}

		if(start.equals(destination)) {
			System.out.println("출발 장소와 도착 장소를 다르게 입력하세요.");
		}

		else {
			// 출발지 존재하는지 검사
			boolean start_find = false;
			for(int i = 0; i < node_name.length; i++) {
				for(int j = 0; j < node_name[i].length; j++) {
					// 입력을 받았는데 출발지,도착지가 리스트에 없을 경우 다시 입력 받도록 한다.
					if(start.equals(node_name[i][j])) {
						start_find = true;
						break;
					}
				}
				if(start_find == true) // 출발지,도착지가 존재하지않으면 for문 다 탈출 시킬려고 작업해놓은거임.
					break;
			}

			// 도착지 존재하는지 검사
			boolean destination_find = false;
			for(int i = 0; i < node_name.length; i++) {
				for(int j = 0; j < node_name[i].length; j++) {
					// 입력을 받았는데 출발지,도착지가 리스트에 없을 경우 다시 입력 받도록 한다.
					if(destination.equals(node_name[i][j])) {
						destination_find = true;
						break;
					}
				}

				if(destination_find == true) // 출발지,도착지가 존재하지않으면 for문 다 탈출 시킬려고 작업해놓은거임.
					break;
			}

			// 출발지,도착지 모두 존재
			if(start_find == true && destination_find == true) {
				System.out.println("===============================");
				getDistance(start, destination);
				if(jc_exist == true) {
					getPath(JC_start, JC_destination);
					System.out.println("===============================");
				}	
			}
			else if(start_find == false && destination_find == false) {
				System.out.println("없는 출발지와 도착지를 입력했습니다.");
			}
			else if(start_find == false) {
				System.out.println("없는 출발지를 입력했습니다.");
			}
			else if(destination_find == false) {
				System.out.println("없는 도착지를 입력했습니다.");
			}
		}
	}
	
	public void getDistance(String start, String destination) { // 최소거리 & 같은 고속도로 내에서 경로 구하기. 없는 지점 입력할 경우 다시 입력받기
		// node_name.length ==> 전국 고속도로 개수, node_name[i].length ==> 각 고속도로별 노드 개수
		// 출발지 : start, 도착지 : destination
		
		ArrayList<Integer> startJC = new ArrayList<Integer>(); // 출발점이 속하는 고속도로의 모든 JC인덱스 저장.
		ArrayList<Integer> destinationJC = new ArrayList<Integer>(); // 도착점이 속하는 고속도로의 모든 JC인덱스 저장.
		
		ArrayList<Double> temp_start_distance = new ArrayList<Double>(); // 출발점 ~ 출발점이 속한 고속도로의 JC까지의 거리들 저장.
		ArrayList<Double> temp_destination_distance = new ArrayList<Double>(); // 도착점 ~ 도착점이 속한 고속도로의 JC까지의 거리들 저장.
		
		// 출발지와 도착지 사이에 JC없을 때 조건 추가해주기
		
		int highway1 = -1;
		int highway2 = -2;
		int index1 = -1;
		int index2 = -1;
		
		for(int i = 0; i < node_name.length; i++) {
			for(int j = 0; j < node_name[i].length; j++) {
				if(node_name[i][j].equals(start)) {
					highway1 = i;
					index1 = j;
				}
				
				if(node_name[i][j].equals(destination)) {
					highway2 = i;
					index2 = j;
				}
				if(highway1 == highway2) {
					break;
				}
			}
			if(highway1 == highway2) {
				break;
			}
		}
		
		if(index1 > index2) {
			for(int i = 0; i <= (index1 - index2); i++) {
				if(node_name[highway1][index2 + i].contains("J")) {
					jc_exist = true;
					break;
				}
			}
		}
		else { // index1 <= index2
			for(int i = 0; i <= (index2 - index1); i++) {
				if(node_name[highway1][index1 + i].contains("J")) {
					jc_exist = true;
					break;
				}
			}
		}
		
		if(jc_exist == false) {
			System.out.print("< 총 이동거리  > : ");
			if(index1 > index2) {
				
				double total_distance = 0;
				for(int i = 0; i < (index1 - index2); i++) {
					total_distance += node_distance[highway1][index2 + i];
				}
				System.out.printf("%.2f km \n", total_distance);
				
				System.out.println("< 이동경로  > ");
				
				for(int i = 0; i <= (index1 - index2); i++) {
					System.out.println((i + 1) + ") " + highway_name[highway1] + node_name[highway1][index2 + i]);
				}
				
			}
			else { // index1 <= index2
				double total_distance = 0;
				for(int i = 0; i < (index2 - index1); i++) {
					total_distance += node_distance[highway1][index1 + i];
				}
				System.out.println(total_distance + " km" + '\n');
				
				System.out.println("< 이동경로  > ");
				
				for(int i = 0; i <= (index2 - index1); i++) {
					System.out.println((i + 1) + ") " + highway_name[highway1] + node_name[highway1][index1 + i]);
				}
				
				
			}
		}
		
		else { // jc_exist == true;
			for(int i = 0; i < node_name.length; i++) {
				for(int j = 0; j < node_name[i].length; j++) {
					if(node_name[i][j].equals(start)) { // 시작점의 index == j
						for(int k = 0; k < junction_index[i].length; k++) { // JC의 인덱스 == junction_index[i][0] ~ [i][k - 1]
							
							if(j < junction_index[i][k]) {
								double temp = 0;
								for(int m = 0; m < (junction_index[i][k] - j); m++) {
									temp += node_distance[i][j + m];
								}
								temp_start_distance.add(temp);
							}
							else {
								double temp = 0;
								for(int m = 0; m < (j - junction_index[i][k]); m++) {
									temp += node_distance[i][junction_index[i][k] + m];
								}
								temp_start_distance.add(temp);
							}
							
							startJC.add(junction_name.indexOf(node_name[i][junction_index[i][k]]));						
						}
						
					}
					else if(node_name[i][j].equals(destination)) {
						
						for(int k = 0; k < junction_index[i].length; k++) {
							
							if(j < junction_index[i][k]) {
								double temp = 0;
								for(int m = 0; m < (junction_index[i][k] - j); m++) {
									temp += node_distance[i][j + m];
								}
								temp_destination_distance.add(temp); // destinationJC.size()개만큼 저장됨.
							}
							else {
								double temp = 0;
								for(int m = 0; m < (j - junction_index[i][k]); m++) {
									temp += node_distance[i][junction_index[i][k] + m];
								}
								temp_destination_distance.add(temp);
							}
							
							destinationJC.add(junction_name.indexOf(node_name[i][junction_index[i][k]]));
						}	
					}		
				}
			}

			double min = Double.MAX_VALUE; 
			for(int i = 0; i < startJC.size(); i++) {
				for(int j = 0; j < destinationJC.size(); j++) {
					double total_distance = dajikstra(startJC.get(i), destinationJC.get(j)) + 
							temp_start_distance.get(i) + temp_destination_distance.get(j);
					
					if(min > total_distance) {
						min = total_distance;
						JC_start = startJC.get(i);
						JC_destination = destinationJC.get(j);
					}

					// temp_distance는 startJC.size() * destinationJC.size()개만큼 저장됨.
				}
			}
			
			System.out.printf("총 이동거리 : %.2f km \n", min);
		
			// return min; // 최소거리 return
		}
		
		
	}
	
	public double dajikstra(int start, int destination) { // JC의 최소길이를 구하기 위한 dajikstra. 인자는 JC의 출발, JC의 도착임.
		int size = junction_distance.length;
		
		boolean[] check = new boolean[size];
		double[] distance = new double[size];
		
		for(int i = 0; i < size; i++) {
			check[i] = false;
		}
		
		check[start] = true;

		for(int i = 0; i < size; i++){
			if(check[i] == false){
				distance[i] = junction_distance[start][i]; // distance값 초기화. distance[i] == junction_distance[start][i]
			}                                              // ==> 이미 되어있다. ==> 할 필요 없음.
		}
		
		for(int i = 0; i < size; i++){ 
			double min = Double.MAX_VALUE;
			int w = start;                
			for(int j = 0; j < size; j++){
				if(check[j] == false){
					if(distance[j] < min){
						min = distance[j]; // distance[i] = junction_distance[start][i] ==> startJC과 iJC까지의 거리
						w = j;
					}
				}
			}
			check[w] = true;
			
			for(int k = 0; k < size; k++){
				if(check[k] == false && junction_distance[w][k] != 0){
					if(distance[k] > distance[w] + junction_distance[w][k]){
						distance[k] = distance[w] + junction_distance[w][k];
					}
				}
			}
		}
		
		return distance[destination];
	}
	
	
	public void getPath(int JC_start, int JC_destination) { // 이동경로를 구하기 위한 dajikstra. 인자는 JC_start, JC_destination임.
		// JC의 경로 구하기 ==> 전체 경로 구하기
		
		LinkedHashMap<Integer, Integer> min_index = new LinkedHashMap<Integer, Integer>(); // 매 단계마다 w값을 저장.
		LinkedHashMap<Integer, Integer> count_of_index = new LinkedHashMap<Integer, Integer>();
		//각 인덱스의 값이 바뀔때마다 몇 단계에서 인덱스가 바뀌었는가 확인.
		
		int size = junction_name.size();
		
		boolean[] check = new boolean[size];
		double[] distance = new double[size];
		
		for(int i = 0; i < size; i++) {
			check[i] = false;
		}
		
		check[JC_start] = true;

		for(int i = 0; i < size; i++){
			if(check[i] == false){
				distance[i] = junction_distance[JC_start][i]; // distance값 초기화. distance[i] == junction_distance[start][i]
			}                                              // ==> 이미 되어있다. ==> 할 필요 없음.
		}
		
		for(int i = 0; i < size; i++){ 
			double min = Double.MAX_VALUE;
			int w = JC_start; 
			
			for(int j = 0; j < size; j++){
				if(check[j] == false && distance[j] != Double.MAX_VALUE){
					if(distance[j] < min){
						// i번째 단계를 진행하기 위한 w값을 결정하는 작업.
						min = distance[j]; // distance[i] = junction_distance[start][i] ==> startJC과 iJC까지의 거리
						w = j;             
						min_index.put(i, w);
					}
				}
			}
			
			check[w] = true;
			
			for(int k = 0; k < size; k++){
				if(check[k] == false) {// && junction_distance[w][k] != 0){
					
					if(distance[k] > (distance[w] + junction_distance[w][k])){
						distance[k] = distance[w] + junction_distance[w][k];
						
						if(count_of_index.get(k) != null) {
							count_of_index.remove(k);
							count_of_index.put(k, i);
						}
						else {
							count_of_index.put(k, i);
						}
					}
				}
			}
		}
		
		ArrayList<Integer> jc_path = new ArrayList<Integer>();
		jc_path.add(JC_destination);
		
		int pre_key = JC_destination;
		int key = JC_destination;

		while(true) {

			if(count_of_index.containsKey(key)) {
				if(count_of_index.get(pre_key) >= count_of_index.get(key)) {
					jc_path.add(min_index.get(count_of_index.get(key)));
					
				}
				else {
					break;
				}
			}
			else {
				break;
			}
			
			pre_key = key;
			key = min_index.get(count_of_index.get(key));		
			
			if(pre_key == key) {
				break;
			}
		}
		
		if(jc_path.contains(JC_start) == false) {
			jc_path.add(JC_start); // jc_path에는 junction_name의 인덱스
		}
		
		ArrayList<String> path = new ArrayList<String>();
		ArrayList<String> highway_path = new ArrayList<String>();
		
		// 출발고속도로JC ~ 출발지까지의 경로
		for(int i = 0; i < node_name.length; i++) {
			int highway1 = -1;
			int highway2 = -2;
			int index1 = -1;
			int index2 = -1;

			for(int j = 0; j < node_name[i].length; j++) {
				if(node_name[i][j].equals(start)) { // 출발지 
					highway1 = i;
					index1 = j;
				}
				if(node_name[i][j].equals(junction_name.get(jc_path.get(jc_path.size() - 1)))){ // 출발고속도로에 있는 JC
					highway2 = i;
					index2 = j;
				}
			}

			if(highway1 == highway2) {
				if(index1 > index2) {
					for(int m = 0; m <= (index1 - index2); m++) {
						if(path.contains(node_name[i][index1 - m]) == false) {
							path.add(node_name[i][index1 - m]);
							highway_path.add(highway_name[i]);
						}
					}
				}
				else { // index1 <= index2
					for(int m = 0; m <= (index2 - index1); m++) {
						if(path.contains(node_name[i][index1 + m]) == false) {
							path.add(node_name[i][index1 + m]);
							highway_path.add(highway_name[i]);
						}
					}
				}
			}
		}
		
		// JC들 사이의 경로
		for(int i = (jc_path.size() - 1); i > 0; i--) {
			int highway1 = -1;
			int highway2 = -2;
			int index1 = 0;
			int index2 = 0;

			for(int j = 0; j < node_name.length; j++) {
			
				for(int k = 0; k < node_name[j].length; k++) {
					if(node_name[j][k].equals(junction_name.get(jc_path.get(i)))) { // 처음 JC
						highway1 = j; // 고속도로 인덱스.
						index1 = k;
					}

					if(node_name[j][k].equals(junction_name.get(jc_path.get(i - 1)))) { // 다음 JC
						highway2 = j;
						index2 = k;
					}
				}
				
				if(highway1 == highway2) {
					break;
				}
			}
			
				if(index1 > index2) {
					
					while(true) {
						
						if(path.contains(node_name[highway1][index1]) == false) {
							path.add(node_name[highway1][index1--]); // index2 ~ index1
							highway_path.add(highway_name[highway1]);
							
							if(index1 <= index2) {
								break;
							}
						}
						else {
							index1--;
							
							if(index1 <= index2) {
								break;
							}
						}
					}
		
				}
				else { // index1 <= index2
					
					
					while(true) {
						if(path.contains(node_name[highway1][index1]) == false) {
							path.add(node_name[highway1][index1++]); // index1 ~ index2
							highway_path.add(highway_name[highway1]);
							
							if(index1 >= index2) {
								break;
							}
						}
						else {
							index1++;
							if(index1 >= index2) {
								break;
							}
						}
					}
					
				}
			
			
			
		}

		// 도착지 ~ 도착고속도로JC의 경로
		for(int i = 0; i < node_name.length; i++) {
			int highway1 = -1;
			int highway2 = -2;
			int index1 = 0;
			int index2 = 0;

			for(int j = 0; j < node_name[i].length; j++) {
				if(node_name[i][j].equals(destination)) { // 도착지 
					highway1 = i;
					index1 = j;
				}

				if(node_name[i][j].equals(junction_name.get(jc_path.get(0)))){ // 도착고속도로에 있는 JC
					highway2 = i;
					index2 = j;
				}
			}

			if(highway1 == highway2) {

				
				if(index1 > index2) {

					
					for(int m = 0; m <= (index1 - index2); m++) {
						if(path.contains(node_name[i][index2 + m]) == false) {
							path.add(node_name[i][index2 + m]);
							highway_path.add(highway_name[i]);
						}
					}
					
				}
				else { // index1 <= index2	
					
					
					for(int m = 0; m <= (index2 - index1); m++) {
						if(path.contains(node_name[i][index2 - m]) == false) {
							path.add(node_name[i][index2 - m]);
							highway_path.add(highway_name[i]);
						}
					}
					
				}
			}
		}

		int count1 = 0;
		System.out.println();
		System.out.println("< 이동경로  >");
		for(int i = 0; i < path.size(); i++) {
			System.out.println((count1 + 1) + ") (" + highway_path.get(i) + ") " + path.get(i));
			count1++;
		}

	}
	
}
