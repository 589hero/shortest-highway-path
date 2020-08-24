import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Saving_highway {
	
	private String[] highway_name = null; // 고속도로 이름 저장.
	
	private String[][] node_name = null; // node_name.length ==> 전국 고속도로 개수, node_name[i].length ==> 각 고속도로별 노드 개수
	private double[][] node_distance = null;
	// private Boolean[][] node_visited = null; // 필요한 변수인가?
	
	private ArrayList<String> junction_name = new ArrayList<String>();
	private int[][] junction_index = null;
	private double[][] junction_distance = null;
	
	private Scanner scan = null;
	
	public Saving_highway() {
	   SavingHighwayInfo();
	   SavingJunctionInfo();
	}
	
	public void SavingHighwayInfo() {
		try {
			
			ArrayList<String> temp_highway_name = new ArrayList<String>();
			ArrayList<ArrayList<String>> temp_node_name = new ArrayList<ArrayList<String>>();
			ArrayList<ArrayList<Double>> temp_node_distance = new ArrayList<ArrayList<Double>>();
			
			File file = new File("고속도로_거리_정리.txt");
			
			scan = new Scanner(file);
		    
			int highway_count = 0;
			while(scan.hasNextLine()) { // 한 번의 loop당 1개의 고속도로를 처리함.
				ArrayList<String> temp_node_name1 = new ArrayList<String>();
				ArrayList<Double> temp_node_distance1 = new ArrayList<Double>();
				temp_highway_name.add(scan.nextLine());
				
				StringTokenizer str = new StringTokenizer(scan.nextLine().trim(), "/"); // 노드 이름 나눔
				
				while(str.hasMoreTokens()) {
					temp_node_name1.add(str.nextToken().trim()); // 노드 이름 저장.
				}
				
				StringTokenizer str1 = new StringTokenizer(scan.nextLine().trim(), "/"); // 노드 간 거리 나눔.
				
				while(str1.hasMoreTokens()) {
					temp_node_distance1.add(Double.parseDouble(str1.nextToken().trim()));
				}
				temp_node_name.add(temp_node_name1);
				temp_node_distance.add(temp_node_distance1);
				
				highway_count++;
			}
			
			highway_name = new String[temp_highway_name.size()];
			for(int i = 0; i < highway_name.length; i++) {
				highway_name[i] = temp_highway_name.get(i);
			}

			node_name = new String[highway_count][];
			node_distance = new double[highway_count][];
			
			for(int i = 0; i < highway_count; i++) {
				node_name[i] = new String[temp_node_name.get(i).size()];
				for(int j = 0; j < temp_node_name.get(i).size(); j++) {
					node_name[i][j] = temp_node_name.get(i).get(j);
				}
			}
			
			for(int i = 0; i < highway_count; i++) {
				node_distance[i] = new double[temp_node_distance.get(i).size()];
				for(int j = 0; j < temp_node_distance.get(i).size(); j++) {
					node_distance[i][j] = temp_node_distance.get(i).get(j);
				}
			}
		    
			scan.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	
	public void SavingJunctionInfo() {
		
	
		// ArrayList<String> temp_junction_name = new ArrayList<String>();
        ArrayList<ArrayList<Integer>> temp_junction_index = new ArrayList<ArrayList<Integer>>();
		
        // 전국 고속도로에 있는 JC들의 이름을 모두 junction_name에다가 저장. 따라서 junction_name.size() ==> 전국에 있는 JC개수(155개) ==> 83개
		for(int i = 0; i < node_name.length; i++){
		    for(int j = 0; j < node_name[i].length; j++){
		        if(node_name[i][j].contains("J")) {
		          if(junction_name.contains(node_name[i][j]) == false){
		        	  junction_name.add(node_name[i][j]); 
		           }
		        }
		    }
		}
		
		for(int i = 0; i < node_name.length; i++){
		    ArrayList<Integer> index = new ArrayList<Integer>();
		    for(int j = 0; j < node_name[i].length; j++){
		        if(node_name[i][j].contains("J")){
		          index.add(j);
		        }		   
		    }
		    temp_junction_index.add(index); // junction_index[0][] 저장. 
		}

		junction_distance = new double[junction_name.size()][junction_name.size()]; // 83개의 JC. 83*83 행렬
		
		// junction_index 생성 및 초기화.
		
		junction_index = new int[node_name.length][];
		for(int i = 0; i < node_name.length; i++) {
			junction_index[i] = new int[temp_junction_index.get(i).size()];
			for(int j = 0; j < junction_index[i].length; j++) {
				junction_index[i][j] = temp_junction_index.get(i).get(j);
			}
		}
		
		// junction_distance 초기화
		for(int i = 0; i < junction_distance.length; i++){
		    for(int j = 0; j < junction_distance.length; j++){
		        junction_distance[i][j] = Double.MAX_VALUE;
		    }
		}
		
		for(int i = 0; i < node_name.length; i++){
			   for(int j = 0; j < junction_index[i].length; j++){
				   
			     for(int k = (j + 1); k < junction_index[i].length; k++){ // 여기서 얻는 인덱스가 더 크다.
			           int count = 0;
			           double distance = 0;
			           while(count < junction_index[i][k] - junction_index[i][j]){
			               distance += node_distance[i][junction_index[i][j] + count];
			               count++;
			           }
			           
			           junction_distance[junction_name.indexOf(node_name[i][junction_index[i][j]])]
			        		   			[junction_name.indexOf(node_name[i][junction_index[i][k]])]
			        		   					= distance;
			           junction_distance[junction_name.indexOf(node_name[i][junction_index[i][k]])]
	        		   					[junction_name.indexOf(node_name[i][junction_index[i][j]])]
	        		   					= distance;
			      }
			   }
			}      
	}
	
	public String[] getHighwayName() {
		return highway_name;
	}
	
	public String[][] getNodeName(){
		return node_name;
	}
	
	public double[][] getNodeDistance(){
		return node_distance;
	}
	
	public ArrayList<String> getJunctionName(){
		return junction_name;
	}
	
	public int[][] getJunctionIndex(){
		return junction_index;
	}
	
	public double[][] getJunctionDistance(){
		return junction_distance;
	};
	
	public void test() {
		
		/*for(int i = 0; i < node_name.length; i++) {
			System.out.println("node_name[i].length : " + node_name[i].length);
			for(int j = 0; j < node_name[i].length; j++) {
				System.out.println(node_name[i][j]);
			}
		}*/
		
		/*for(int i = 0; i < node_name.length; i++) {
			for(int j = 0; j < node_distance[i].length; j++) {
				System.out.println(node_name[i][j] + " - " + node_name[i][j + 1] +
						" 간의 거리 : " + node_distance[i][j] + "km");
			}
		}*/
		
		/*for(int i = 0; i < junction_name.size(); i++) {
			System.out.println("JC의 이름 : " + junction_name.get(i));
		}*/
		
		for(int i = 0; i < junction_distance.length; i++) {
			for(int j = 0; j < junction_distance.length; j++) {
				if(junction_distance[i][j] != Double.MAX_VALUE) {
					System.out.printf("%s - %s간의 거리 : %.2f km \n",
							junction_name.get(i), junction_name.get(j), junction_distance[i][j]);
				}
			}
		}
		
		/*for(int i = 0; i < node_name.length; i++) {
			System.out.println((i + 1) + "번째 고속도로의 노드 개수는 " + node_name[i].length + "이고," +
			              "distance의 개수는 " + node_distance[i].length + "입니다.");
		}*/
	}
}
