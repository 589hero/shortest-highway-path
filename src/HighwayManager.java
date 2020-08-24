import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Scanner;

public class HighwayManager {

	private String[] highway_name = null;
	
	private String[][] node_name = null;
	private double[][] node_distance = null;
	// node_name.length ==> ���� ��ӵ��� ����, node_name[i].length ==> �� ��ӵ��κ� ��� ����

	private ArrayList<String> junction_name = null; // JC�̸� ����. ==> 83��
	private int[][] junction_index = null;          // ��ӵ��ο��� JC�ε����� ����.
	private double[][] junction_distance = null;    // dajikstra�� ���� JC�Ÿ� ����. 83*83
	
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
			System.out.println("1) �ּҰ�� ã��  2) ����");
			int menu = -1;
			
			while(true) {
				System.out.print("�޴��� �����ϼ��� ==> ");
				menu = scan.nextInt();
				System.out.println("===============================");
					
				if(menu == 1 || menu == 2) {
					break;
				}
				else {
					System.out.println("1 �Ǵ� 2�� �Է��ϼ��� : ");
				}
			}
			
			switch(menu) {
			case 1 :
				menu1();
				break;
			case 2 :
				System.out.println("�ȳ����輼��.");
				System.exit(0);
			}
			
			System.out.println();
			System.out.print("���θ޴��� �����ðڽ��ϱ�? (y / n) : ");
			char ch = scan.next().charAt(0);
			System.out.println("===============================");
			
			if(ch == 'y') {
				
			}
			else {
				System.out.println("�� �б⵿�� ������� �������ּż� �����մϴ�.");
				break;
			}
		}
	}
	
	public void menu1() {
		/*for(int i = 0; i < highway_name.length; i++) {
			System.out.println((i) + ") " + highway_name[i]);
		}*/
		
		while(true) {
			System.out.print("������� �Է��ϼ��� : ");
			start = scan.next();
			
			if(start.equals("����") || start.equals("����")) {
				System.out.println("�ٽ� ����ּ���.");
			}
			else {
				break;
			}
			
		}
		
		// System.out.println(start);
		while(true) {
			System.out.print("�������� �Է��ϼ��� : ");
			destination = scan.next();
			
			if(destination.equals("����") || destination.equals("����")) {
				System.out.println("�ٽ� ����ּ���.");
			}
			else {
				break;
			}	
		}

		if(start.equals(destination)) {
			System.out.println("��� ��ҿ� ���� ��Ҹ� �ٸ��� �Է��ϼ���.");
		}

		else {
			// ����� �����ϴ��� �˻�
			boolean start_find = false;
			for(int i = 0; i < node_name.length; i++) {
				for(int j = 0; j < node_name[i].length; j++) {
					// �Է��� �޾Ҵµ� �����,�������� ����Ʈ�� ���� ��� �ٽ� �Է� �޵��� �Ѵ�.
					if(start.equals(node_name[i][j])) {
						start_find = true;
						break;
					}
				}
				if(start_find == true) // �����,�������� �������������� for�� �� Ż�� ��ų���� �۾��س�������.
					break;
			}

			// ������ �����ϴ��� �˻�
			boolean destination_find = false;
			for(int i = 0; i < node_name.length; i++) {
				for(int j = 0; j < node_name[i].length; j++) {
					// �Է��� �޾Ҵµ� �����,�������� ����Ʈ�� ���� ��� �ٽ� �Է� �޵��� �Ѵ�.
					if(destination.equals(node_name[i][j])) {
						destination_find = true;
						break;
					}
				}

				if(destination_find == true) // �����,�������� �������������� for�� �� Ż�� ��ų���� �۾��س�������.
					break;
			}

			// �����,������ ��� ����
			if(start_find == true && destination_find == true) {
				System.out.println("===============================");
				getDistance(start, destination);
				if(jc_exist == true) {
					getPath(JC_start, JC_destination);
					System.out.println("===============================");
				}	
			}
			else if(start_find == false && destination_find == false) {
				System.out.println("���� ������� �������� �Է��߽��ϴ�.");
			}
			else if(start_find == false) {
				System.out.println("���� ������� �Է��߽��ϴ�.");
			}
			else if(destination_find == false) {
				System.out.println("���� �������� �Է��߽��ϴ�.");
			}
		}
	}
	
	public void getDistance(String start, String destination) { // �ּҰŸ� & ���� ��ӵ��� ������ ��� ���ϱ�. ���� ���� �Է��� ��� �ٽ� �Է¹ޱ�
		// node_name.length ==> ���� ��ӵ��� ����, node_name[i].length ==> �� ��ӵ��κ� ��� ����
		// ����� : start, ������ : destination
		
		ArrayList<Integer> startJC = new ArrayList<Integer>(); // ������� ���ϴ� ��ӵ����� ��� JC�ε��� ����.
		ArrayList<Integer> destinationJC = new ArrayList<Integer>(); // �������� ���ϴ� ��ӵ����� ��� JC�ε��� ����.
		
		ArrayList<Double> temp_start_distance = new ArrayList<Double>(); // ����� ~ ������� ���� ��ӵ����� JC������ �Ÿ��� ����.
		ArrayList<Double> temp_destination_distance = new ArrayList<Double>(); // ������ ~ �������� ���� ��ӵ����� JC������ �Ÿ��� ����.
		
		// ������� ������ ���̿� JC���� �� ���� �߰����ֱ�
		
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
			System.out.print("< �� �̵��Ÿ�  > : ");
			if(index1 > index2) {
				
				double total_distance = 0;
				for(int i = 0; i < (index1 - index2); i++) {
					total_distance += node_distance[highway1][index2 + i];
				}
				System.out.printf("%.2f km \n", total_distance);
				
				System.out.println("< �̵����  > ");
				
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
				
				System.out.println("< �̵����  > ");
				
				for(int i = 0; i <= (index2 - index1); i++) {
					System.out.println((i + 1) + ") " + highway_name[highway1] + node_name[highway1][index1 + i]);
				}
				
				
			}
		}
		
		else { // jc_exist == true;
			for(int i = 0; i < node_name.length; i++) {
				for(int j = 0; j < node_name[i].length; j++) {
					if(node_name[i][j].equals(start)) { // �������� index == j
						for(int k = 0; k < junction_index[i].length; k++) { // JC�� �ε��� == junction_index[i][0] ~ [i][k - 1]
							
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
								temp_destination_distance.add(temp); // destinationJC.size()����ŭ �����.
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

					// temp_distance�� startJC.size() * destinationJC.size()����ŭ �����.
				}
			}
			
			System.out.printf("�� �̵��Ÿ� : %.2f km \n", min);
		
			// return min; // �ּҰŸ� return
		}
		
		
	}
	
	public double dajikstra(int start, int destination) { // JC�� �ּұ��̸� ���ϱ� ���� dajikstra. ���ڴ� JC�� ���, JC�� ������.
		int size = junction_distance.length;
		
		boolean[] check = new boolean[size];
		double[] distance = new double[size];
		
		for(int i = 0; i < size; i++) {
			check[i] = false;
		}
		
		check[start] = true;

		for(int i = 0; i < size; i++){
			if(check[i] == false){
				distance[i] = junction_distance[start][i]; // distance�� �ʱ�ȭ. distance[i] == junction_distance[start][i]
			}                                              // ==> �̹� �Ǿ��ִ�. ==> �� �ʿ� ����.
		}
		
		for(int i = 0; i < size; i++){ 
			double min = Double.MAX_VALUE;
			int w = start;                
			for(int j = 0; j < size; j++){
				if(check[j] == false){
					if(distance[j] < min){
						min = distance[j]; // distance[i] = junction_distance[start][i] ==> startJC�� iJC������ �Ÿ�
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
	
	
	public void getPath(int JC_start, int JC_destination) { // �̵���θ� ���ϱ� ���� dajikstra. ���ڴ� JC_start, JC_destination��.
		// JC�� ��� ���ϱ� ==> ��ü ��� ���ϱ�
		
		LinkedHashMap<Integer, Integer> min_index = new LinkedHashMap<Integer, Integer>(); // �� �ܰ踶�� w���� ����.
		LinkedHashMap<Integer, Integer> count_of_index = new LinkedHashMap<Integer, Integer>();
		//�� �ε����� ���� �ٲ𶧸��� �� �ܰ迡�� �ε����� �ٲ���°� Ȯ��.
		
		int size = junction_name.size();
		
		boolean[] check = new boolean[size];
		double[] distance = new double[size];
		
		for(int i = 0; i < size; i++) {
			check[i] = false;
		}
		
		check[JC_start] = true;

		for(int i = 0; i < size; i++){
			if(check[i] == false){
				distance[i] = junction_distance[JC_start][i]; // distance�� �ʱ�ȭ. distance[i] == junction_distance[start][i]
			}                                              // ==> �̹� �Ǿ��ִ�. ==> �� �ʿ� ����.
		}
		
		for(int i = 0; i < size; i++){ 
			double min = Double.MAX_VALUE;
			int w = JC_start; 
			
			for(int j = 0; j < size; j++){
				if(check[j] == false && distance[j] != Double.MAX_VALUE){
					if(distance[j] < min){
						// i��° �ܰ踦 �����ϱ� ���� w���� �����ϴ� �۾�.
						min = distance[j]; // distance[i] = junction_distance[start][i] ==> startJC�� iJC������ �Ÿ�
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
			jc_path.add(JC_start); // jc_path���� junction_name�� �ε���
		}
		
		ArrayList<String> path = new ArrayList<String>();
		ArrayList<String> highway_path = new ArrayList<String>();
		
		// ��߰�ӵ���JC ~ ����������� ���
		for(int i = 0; i < node_name.length; i++) {
			int highway1 = -1;
			int highway2 = -2;
			int index1 = -1;
			int index2 = -1;

			for(int j = 0; j < node_name[i].length; j++) {
				if(node_name[i][j].equals(start)) { // ����� 
					highway1 = i;
					index1 = j;
				}
				if(node_name[i][j].equals(junction_name.get(jc_path.get(jc_path.size() - 1)))){ // ��߰�ӵ��ο� �ִ� JC
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
		
		// JC�� ������ ���
		for(int i = (jc_path.size() - 1); i > 0; i--) {
			int highway1 = -1;
			int highway2 = -2;
			int index1 = 0;
			int index2 = 0;

			for(int j = 0; j < node_name.length; j++) {
			
				for(int k = 0; k < node_name[j].length; k++) {
					if(node_name[j][k].equals(junction_name.get(jc_path.get(i)))) { // ó�� JC
						highway1 = j; // ��ӵ��� �ε���.
						index1 = k;
					}

					if(node_name[j][k].equals(junction_name.get(jc_path.get(i - 1)))) { // ���� JC
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

		// ������ ~ ������ӵ���JC�� ���
		for(int i = 0; i < node_name.length; i++) {
			int highway1 = -1;
			int highway2 = -2;
			int index1 = 0;
			int index2 = 0;

			for(int j = 0; j < node_name[i].length; j++) {
				if(node_name[i][j].equals(destination)) { // ������ 
					highway1 = i;
					index1 = j;
				}

				if(node_name[i][j].equals(junction_name.get(jc_path.get(0)))){ // ������ӵ��ο� �ִ� JC
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
		System.out.println("< �̵����  >");
		for(int i = 0; i < path.size(); i++) {
			System.out.println((count1 + 1) + ") (" + highway_path.get(i) + ") " + path.get(i));
			count1++;
		}

	}
	
}
