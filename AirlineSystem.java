import java.util.*;
import java.io.*;

final public class AirlineSystem implements AirlineInterface {
  private String [] cityNames = null;
  private static final int INFINITY = Integer.MAX_VALUE;
  private Digraph G = null;
  Route route; 

  public boolean loadRoutes(String fileName){ //WORKING!
  	try{
    Scanner fileScan = new Scanner(new FileInputStream(fileName));
    int v = Integer.parseInt(fileScan.nextLine());
    G = new Digraph(v);
     
    cityNames = new String[v];
    for(int i=0; i<v; i++){
      cityNames[i] = fileScan.nextLine();
    }

    while(fileScan.hasNext()){
      int from = fileScan.nextInt();
      int to = fileScan.nextInt();
      int weight = fileScan.nextInt();
      int cost = (int) (fileScan.nextDouble());
      
      G.addEdge(new WeightedUndirectedEdge(from-1, to-1, weight, cost)); // change this to Set<Integer>
      G.addEdge(new WeightedUndirectedEdge(to-1, from-1,  weight, cost)); // change this to Set<Integer>
      if(fileScan.hasNext()) fileScan.nextLine();
    }
    fileScan.close();
    return true;
    }
    catch(IOException exception){
    return false;
    }
  }
    

  public Set<String> retrieveCityNames() { //WORKING!
  	Set<String> sets = new HashSet<String>();
	for(int i = 0; i < cityNames.length; i++){
		sets.add(cityNames[i]);
	}
    return sets;
  }

  public Set<Route> retrieveDirectRoutesFrom(String city) //WORKING!
    throws CityNotFoundException {
    try{
    int start = 0; //start index
    Route route;
    Set<Route> set = new HashSet<Route>();
    for(int i = 0; i < cityNames.length; i++){ // getting the start index
    	if(cityNames[i].equals(city)) start = i;
    } 
	int x = 0;
	Iterable<WeightedUndirectedEdge> it = G.adj(start);  // iterable for weightedundirectededge
    for (WeightedUndirectedEdge e : it) {
        route = new Route(city, cityNames[e.to()], e.weight(), e.price);
         set.add(route);
    }
    return set;
    }
    catch(Exception e){
    return new HashSet<Route>();
    }
  }

  public Set<ArrayList<String>> fewestStopsItinerary(String source,
    String destination) throws CityNotFoundException { //WORKING!
     try{
      //Use the index values for source and destination cities
      int sourceNum = -1;
      int destNum = -1;
      //Create set and arraylist variables needed 
      Set<ArrayList<String>> set = new HashSet<ArrayList<String>>();
      ArrayList<String> shortList = new ArrayList<String>();
      
      //get the index values for source and destination cities!
      for(int i = 0; i < cityNames.length; i++){
      	if(cityNames[i].equals(source)){
      		sourceNum = i;
      	}
      	if(cityNames[i].equals(destination)){
      		destNum = i;
      	}
      }
      if(sourceNum == -1 || destNum == -1) return set;

      G.bfs(sourceNum); // run bfs on source city!
            
      Stack<Integer> path = new Stack<>(); // Use a stack to construct the shortest path from the edgeTo array
      for (int x = destNum; x != sourceNum; x = G.edgeTo[x]){
    	path.push(x);
      }
      
      shortList.add(cityNames[sourceNum]); // add the cities onto the list
                                   
      int prevVertex = sourceNum;
      while(!path.empty()){  
        int v = path.pop();
        shortList.add(cityNames[v]);
        prevVertex = v;
      }
	set.add(shortList);
	return set;
	}
	catch(Exception e){
		return new HashSet<ArrayList<String>>();
	}
	
  }
    //shortest hops 


  public Set<ArrayList<Route>> shortestDistanceItinerary(String source,
    String destination) throws CityNotFoundException { //WORKING!
      //Use the index values for source and destination cities
       int sourceNum = -1;
       int destNum = -1;
      //Create set and arraylist variables needed 
       Set<ArrayList<Route>> set = new HashSet<ArrayList<Route>>();
       ArrayList<Route> shortList = new ArrayList<Route>();
      
      //get the index values for source and destination cities!
       for(int i = 0; i < cityNames.length; i++){
      	 if(cityNames[i].equals(source)){
      		sourceNum = i;
      	 }
      	 if(cityNames[i].equals(destination)){
      		destNum = i;
      	 }
       }
       
       if(sourceNum == -1 || destNum == -1) return set; // if the city or destination does not exist return an empty set

       G.dijkstras(sourceNum, destNum); // run dijkstras on source and destination according to distance rather than price
        
       Stack<Integer> path = new Stack<>();
       for (int x = destNum; x != sourceNum; x = G.edgeTo[x]){
            path.push(x);
       }
       
       int prevVertex = sourceNum;
       
       while(!path.empty()){
       		int v = path.pop();
            shortList.add(new Route(cityNames[prevVertex], cityNames[v], G.distTo[v] - G.distTo[prevVertex], G.priceTo[v] - G.priceTo[prevVertex]));
            prevVertex = v;
          }
    //shortest path / distance 
	set.add(shortList);
    return set;
  }

  public Set<ArrayList<Route>> shortestDistanceItinerary(String source,
    String transit, String destination) throws CityNotFoundException { //NEEDS WORK!
       Set<ArrayList<Route>> ret = new HashSet<ArrayList<Route>>();
       Set<ArrayList<Route>> set1 = shortestDistanceItinerary(source, transit);
       Set<ArrayList<Route>> set2 = shortestDistanceItinerary(transit, destination);
	   ArrayList<Route> shortList = new ArrayList<Route>();
	   
	   shortList=set1.iterator().next();
	   shortList.addAll(set2.iterator().next());
	   ret.add(shortList);
// 	   ret.add(set2.iterator().next());
	   
       //ret.add(shortList);
    return ret;
    //"What is the shortest path from A to B given that I want to stop at C for a while?"
    //return null;
  }

  public boolean addCity(String city){ //WORKING!
  	int i = 0;
  	while(i < cityNames.length){
  		if(city.equals(cityNames[i])) return false; 
  		i++;
  	}
  	cityNames = upsize(cityNames);
  	cityNames[i] = city;
  	
  	Digraph temp = new Digraph(cityNames.length);
  	for(int x = 0; x < cityNames.length-1; x++){
	Iterable<WeightedUndirectedEdge> it = G.adj(x);  // iterable for weightedundirectededge
    for (WeightedUndirectedEdge e : it) {
    	temp.addEdge(new WeightedUndirectedEdge(e.v, e.w, e.weight, e.price)); // change this to Set<Integer>
     	temp.addEdge(new WeightedUndirectedEdge(e.w, e.v,  e.weight, e.price)); // change this to Set<Integer>
  	}
  	}
  	G = temp;
    return true;
  }
  
  public String[] upsize(String[] array){ // upsize array
  	String[] newArray = new String[array.length + 1];
  	for(int i = 0; i < array.length; i++){
  		newArray[i] = array[i];
  	}
  	return newArray; 
  }


  public boolean addRoute(String source, String destination, int distance,
    double price) throws CityNotFoundException { //WORKING!
    try{
	int start = 0;
    for(int i = 0; i < cityNames.length; i++){ // getting the start index
    	if(cityNames[i].equals(source)) start = i;
    } //getting start index
    int sourceNum = -1;
    int destNum = -1;
    //get the index values for source and destination cities!
    for(int i = 0; i < cityNames.length; i++){
      if(cityNames[i].equals(source)){
      		sourceNum = i;
      }
      if(cityNames[i].equals(destination)){
      		destNum = i;
      }
    }

    Iterable<WeightedUndirectedEdge> iter = G.adj(start);  //iterator
    Route routeT = new Route(source, destination, distance, price); //temp route
    Route temp;
    
    for (WeightedUndirectedEdge e : iter) {
    	temp = new Route(source, cityNames[e.to()], e.weight(), e.price);
    	if(temp.equals(routeT)) return false;
    }
    //if the route doesn't exist already -- add the route here 
    G.addEdge(new WeightedUndirectedEdge(sourceNum, destNum, distance, price));
    G.addEdge(new WeightedUndirectedEdge(destNum, sourceNum, distance, price));

    return true;
    }
    catch(Exception e){
    	return false;
    }
  }
  

  public boolean updateRoute(String source, String destination, int distance,
    double price) throws CityNotFoundException { //WORKING!
    try{
    int sourceNum = -1;
    int destNum = -1;
    //get the index values for source and destination cities!
    for(int i = 0; i < cityNames.length; i++){
      if(cityNames[i].equals(source)){
      		sourceNum = i;
      }
      if(cityNames[i].equals(destination)){
      		destNum = i;
      }
    }

    Route r; //temp route
    Route routeT = new Route(source, destination, distance, price); //temp route
    Iterable<WeightedUndirectedEdge> sNum = G.adj(sourceNum);  //source iterator
    Iterable<WeightedUndirectedEdge> dNum = G.adj(destNum);  // destination iterator
    
    
    Route sourceOld = routeT; //old source route = new route
    Route destOld = routeT; //old source route = new route
    
    int found = 0; //if found = 0; not found IF found = 1; found
    
    for (WeightedUndirectedEdge e : sNum) { //TAKE THIS FOR DELETE EDGE AND DELETE ROUTE!!!!!!
    	r = new Route(source, cityNames[e.to()], e.weight(), e.price);
    	if(r.destination.equals(destination)){
    		found = 1;
    		sourceOld = r;
    	    G.deleteEdge(e);
    		break;
    	}
    }
     if(found == 0) return false;
     found = 0;
     
    for (WeightedUndirectedEdge e : dNum) {
    	r = new Route(source, cityNames[e.to()], e.weight(), e.price);
    	if(r.destination.equals(source)){
    		found = 1;
    		destOld = r;
    		G.deleteEdge(e);
    		break;
    	}
    }
    
    if(found == 0) return false;
    
    G.addEdge(new WeightedUndirectedEdge(sourceNum,destNum,distance,price));
    G.addEdge(new WeightedUndirectedEdge(destNum,sourceNum,distance,price));
    return true;
    }
    catch(Exception e){
    	return false;
    }

  }


  public class Digraph {
    private final int v;
    private int e;
    private LinkedList<WeightedUndirectedEdge>[] adj;
    private boolean[] marked;  // marked[v] = is there an s-v path
    private int[] edgeTo;      // edgeTo[v] = previous edge on shortest s-v path
    private int[] distTo;      // distTo[v] = number of edges shortest s-v path
    private double[] priceTo; 	// cost of the flight


    /**
    * Create an empty digraph with v vertices.
    */
    public Digraph(int v) {
      if (v < 0) throw new RuntimeException("Number of vertices must be nonnegative");
      this.v = v;
      this.e = 0;
      @SuppressWarnings("unchecked")
      LinkedList<WeightedUndirectedEdge>[] temp =
      (LinkedList<WeightedUndirectedEdge>[]) new LinkedList[v];
      adj = temp;
      for (int i = 0; i < v; i++)
        adj[i] = new LinkedList<WeightedUndirectedEdge>();
    }

    /**
    * Add the edge e to this digraph.
    */
    public void addEdge(WeightedUndirectedEdge edge) {
      int from = edge.from();
      adj[from].add(edge);
      e++;
    }
    
    public void deleteEdge(WeightedUndirectedEdge edge){
      int from = edge.from();
      adj[from].remove(edge);
      e--;
    }


    /**
    * Return the edges leaving vertex v as an Iterable.
    * To iterate over the edges leaving vertex v, use foreach notation:
    * <tt>for (WeightedUndirectedEdge e : graph.adj(v))</tt>.
    */
    public Iterable<WeightedUndirectedEdge> adj(int v) {
      return adj[v];
    }

    public void bfs(int source) {
      marked = new boolean[this.v];
      distTo = new int[this.e];
      edgeTo = new int[this.v];

      Queue<Integer> q = new LinkedList<Integer>();
      for (int i = 0; i < v; i++){
        distTo[i] = INFINITY;
        marked[i] = false;
      }
      distTo[source] = 0;
      marked[source] = true;
      q.add(source);

      while (!q.isEmpty()) {
        int v = q.remove();
        for (WeightedUndirectedEdge w : adj(v)) {
          if (!marked[w.to()]) {
            edgeTo[w.to()] = v;
            distTo[w.to()] = distTo[v] + 1;
            marked[w.to()] = true;
            q.add(w.to());
          }
        }
      }
    }
    public void dijkstras(int source, int destination) {
      marked = new boolean[this.v];
      distTo = new int[this.v];
      edgeTo = new int[this.v];
      priceTo = new double[this.v];


      for (int i = 0; i < v; i++){
        distTo[i] = INFINITY;
        priceTo[i] = INFINITY;
        marked[i] = false;
      }
      distTo[source] = 0;
      priceTo[source] = 0;
      marked[source] = true;
      int nMarked = 1;

      int current = source;
      while (nMarked < this.v) {
        for (WeightedUndirectedEdge w : adj(current)) {
          if (distTo[current]+w.weight() < distTo[w.to()]) {
	      //TODO:update edgeTo and distTo //go over all the neigherbors of vertex -- then check distance of neighbor is greather than dis cur -- update 
	      	edgeTo[w.to()] = current;
	      	distTo[w.to()] = distTo[current] + w.weight();
	      	priceTo[w.to()] = priceTo[current] + w.price();
	      
          }
        }
        //Find the vertex with minimim path distance
        //This can be done more effiently using a priority queue!
        int min = INFINITY;
        current = -1;

        for(int i=0; i<distTo.length; i++){
          if(marked[i])
            continue;
          if(distTo[i] < min){
            min = distTo[i];
            current = i;
          }
        }
        if(current == -1){
        	break;
        }
        else{
        	marked[current] = true;
        	nMarked++;
        }
		/// update number of marked vertices --- 
	//TODO: Update marked[] and nMarked. Check for disconnected graph.
      }
    }
  }

  /**
  *  The <tt>WeightedUndirectedEdge</tt> class represents a weighted edge in an directed graph.
  */

  public class WeightedUndirectedEdge {
    private final int v;
    private final int w;
    private int weight;
    private double price;
    /**
    * Create a directed edge from v to w with given weight.
    */
    public WeightedUndirectedEdge(int v, int w, int weight, double price) {
      this.v = v;
      this.w = w;
      this.weight = weight;
      this.price = price;
    }

    public int from(){
      return v;
    }

    public int to(){
      return w;
    }

    public int weight(){
      return weight;
    }
    
    public double price(){
    	return price; 
    }
    
  }
}

