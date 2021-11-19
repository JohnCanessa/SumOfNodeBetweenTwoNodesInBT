import java.util.*;

/**
 * Computation of LCA has many applications such as 
 * finding distance between pair of nodes in a tree.
 */

 
public class LowestCommonAncestor {

    // **** ****
    final int MOD = (1000000000 + 7);

    // **** class members ****
    int eulerIndex;                         // Euler index
    int n;                                  // total number of nodes in the tree
    int rootNode;                           // root node

    ArrayList<ArrayList<Integer>> adj;      // adjacency list representing the tree

    int depth[];                             // node depth
    int EulerTour[];                        // used to store the Euler Tour 
    int level[];                            // used by square root decomposition algorithm
    int lookup[][];                         // lookup table for Euler Tree array

    int P[];                                // used by square root decomposition algorithm
    int parent[];                           // node parent list
    boolean vis[];                          // node has been visited array of flags


    /**
     * Constructor
     */
    public LowestCommonAncestor(int n) {

        // **** ****
        this.eulerIndex = 1;
        this.n          = n;
        this.rootNode   = Integer.MAX_VALUE;

        // **** ****
        this.adj = new ArrayList<>();
        for (int i = 0; i <= this.n ; i++) {
            adj.add(new ArrayList<>());
        }

        this.EulerTour  = new int[2 * n];
        this.lookup     = null;
        this.P          = new int[n + 1];
        this.vis        = new boolean[n + 1];

        this.level      = new int[n + 1];
        this.parent     = new int[n + 1];
    }


    /**
     * Generate a string representation of the LCA instance.
     */
    @Override
    public String toString() {

        // **** for performance ****
        StringBuilder sb = new StringBuilder();

        sb.append("eulerIndex: " + this.eulerIndex);
        sb.append("\n         n: " + this.n);
        sb.append("\n  rootNode: " + this.rootNode);

        sb.append("\n       adj: " + this.adj.toString());

        sb.append("\n EulerTour: " + Arrays.toString(this.EulerTour));

        sb.append("\n    lookup: " + Arrays.deepToString(lookup));
        sb.append("\n         P: " + Arrays.toString(this.P));
        sb.append("\n       vis: " + Arrays.toString(this.vis));

        sb.append("\n     level: " + Arrays.toString(this.level));
        sb.append("\n    parent: " + Arrays.toString(this.parent));

        sb.append("\n             ");
        for (int i = 0; i < n + 1; i++)
            sb.append(i + "  ");

        // **** return a string ****
        return sb.toString();
    }


    /**
     * Get the value of the root node.
     * Should be 1.
     */
    public int getRootNode() {

        // **** validate root node ****
        if (this.rootNode != 1) {
            throw new IllegalArgumentException("EXCEPTION <<< rootNode: " + this.rootNode);
        }

        // **** should be 1 ****
        return this.rootNode;
    }


    /**
     * Traverse the tree and populate the parent array such that the 
     * pre-order traversal begins by calling GetParents(rootNode, -1).
     * 
     * This is a recursive method.
     * 
     * This method is NOT used!!!
     */
    public void genParents(int node, int par) {

        // ???? ????
        System.out.println("genParents <<< node: " + node + " par: " + par);

        // **** clear the visited array (if needed) ****
        if (par == -1) {
            Arrays.fill(this.vis, false);
        }

        // **** flag we visited this node ****
        this.vis[node] = true;

        // **** get the children list for this node ****
        ArrayList<Integer> children = this.adj.get(node);

        // ???? ????
        System.out.println("genParents <<< children.size(): " + children.size());

        // **** traverse all children of this node ****
        for (int i = 0; i < children.size(); i++) {

            // ???? ????
            System.out.println("genParents <<< children.get(" + i + "): " + children.get(i) + " par: " + par);

            // **** ****
            if (this.parent[children.get(i)] != par) {

                // **** ****
                this.parent[children.get(i)] = node;

                // **** ****
                genParents(children.get(i), node);
            }
        }
    }


    /**
     * Traverse the tree and populate the parent array,
     * This method is non-recursive.
     */
    public void genParents () {

        // **** clear the visited nodes array ****
        Arrays.fill(this.vis, false);

        // **** traverse the adjacency map ****
        for (int p = 1; p < this.adj.size(); p++) {

            // **** get the children for this node ****
            ArrayList<Integer> children = this.adj.get(p);

            // **** for each child flag this node is the parent (if needed) ****
            for (int chld : children) {
                if (vis[chld] == false) {
                    parent[chld] = p;
                }
            }

            // **** flag this node as visited ****
            this.vis[p] = true;
        }
    }


    /**
     * Naive algorithm.
     * Time complexity per query = O(n)
     * 
     * From the first vertex u, we go all the way up to the 
     * root of the tree and record all the vertices traversed 
     * along the way. From the second vertex v, we also go 
     * all the way up to the root of the tree, but this time 
     * we stop if we encounter a common vertex for the first time.
     */
    public int naiveLCA(int u, int v) {

        // **** initialization ****
        int lca = 0;
        Arrays.fill(this.vis, false);

        // **** validate arguments ****
        if ((u <= 0) || (v <= 0) || (u > n) || (v > n)) {
            throw new IllegalArgumentException("EXCEPTION <<< u: " + u + " v: " + v);
        }

        // **** from u go all the way up to the root recording all vertices ****
        while (true) {

            // **** flag we have visited this node ****
            this.vis[u] = true;

            // **** check if we reached the root node ****
            if (u == this.rootNode) {
                break;
            }

            // **** move up to the parent node ****
            u = parent[u];
        }

        // **** from v go all the way up to the root stopping 
        //      if we encounter a vertex from the previous pass ****
        while (true) {

            // **** check if we found the LCA ****
            if (vis[v]) {
                lca = v;
                break;
            }

            // **** move up to the parent node ****
            v = parent[v];
        }

        // **** return the LCA ****
        return lca;
    }


    /**
     * Fill lookup array lookup[len][len] for all possible values of query ranges.
     * 
     * Does not work because the Euler Tree contains duplicates !!!
     */
    public void preprocess() { 

        // **** ****
        int len = EulerTour.length;

        // **** allocate and generate lookup table (if needed) ****
        if (lookup == null) {

            // ???? ????
            // System.out.println("preprocess <<< EulerTour.length: " + len);

            // **** ****
            lookup = new int[len][len];
        } else {
            return;
        }

        // **** initialize main diagonal (distance to itself: length 1) ****
        for (int i = 0; i < len; i++) 
            lookup[i][i] = i; 
    
        // **** fill rest of the entries in bottom up manner ****
        for (int i = 0; i < len; i++) { 

            // **** fill rest of entries in bottom up manner ****
            for (int j = i + 1; j < len; j++) {
                if (EulerTour[lookup[i][j - 1]] < EulerTour[j]) 
                    lookup[i][j] = lookup[i][j - 1]; 
                else
                    lookup[i][j] = j; 
            }
        }

        // ???? ????
        // System.out.println("preprocess <<< lookup:");
        // for (int i = 1; i < len; i++) {
        //     for (int j = 1; j < len; j++) {
        //         System.out.print(lookup[i][j] + " ");
        //     }
        //     System.out.println();
        // }
    } 


    /**
     * We have an array arr[0 ... n - 1].
     * The values in the array are unique (no repetitions allowed like in the Euler Tree).
     * 
     * We should be able to efficiently find the minimum value 
     * from index L (query start) to R (query end) where 0 <= L <= R <= n-1. 
     * Consider a situation when there are many range queries.
     * 
     * u & v are  indices into the array.
     */
    public int squareRootLCA(int L, int R) {

        // **** ****
        int lca = -1;

        // **** ****
        return lca;
    }


    /**
     * This method populates the level for each node and
     * updated the level array.
     * 
     * The root node is at level 1.
     * 
     * This is a recursive method.
     */
    public void genLevels(int node, int depth) {

        // **** skip if visited ****
        if (this.vis[node])
            return;
    
        // ???? ????
        // System.out.println("genLevels <<< node: " + node + " depth: " + depth);

        // **** flag as visited ****
        this.vis[node] = true;

        // **** set the level for this node ****
        this.level[node] = depth;

        // **** get the count of children ****
        int count = adj.get(node).size();
        if (count == 0)
            return;

        // **** get the children of this node ****
        ArrayList<Integer> children = adj.get(node);

        // **** traverse all the children ****
        for (int child : children) {
            genLevels(child, depth + 1);
        }
    }


    /**
     * Should generate level and parent lists just once.
     * 
     * NOT used!!!
     */
    public int wingItLCA(int u, int v) {

        // ???? ????
        System.out.println("wingItLCA <<< u: " + u + " v: " + v);
       
        // **** for starters ****
        int lca = 0;

        // **** ****
        return lca;
    }


    /**
     * Generate Euler Tree.
     * Recursive method.
     */
    public void eulerTree(int u) {

        // **** flag as visited ****
        this.vis[u] = true;

        // **** ****
        this.EulerTour[this.eulerIndex++] = u;

        // **** ****
        for (int it : adj.get(u)) {
            if (!vis[it]) {
                eulerTree(it);
                this.EulerTour[this.eulerIndex++] = u;
            }
        }
    }


    /**
     * Print Euler Tour Tree
     */
    public void printEulerTour() {
        for (int i = 1; i < this.EulerTour.length; i++) {
            System.out.print(this.EulerTour[i] + " ");
        }
        System.out.println();
    }


    /**
     * Add edge to tree.
     */
    public void addEdge(int u, int v) {

        // **** check node values ****
        if (Math.max(u, v) > n) {
            throw new IllegalArgumentException("EXCEPTION <<< u: " + u + " or v: " + v + " > n: " + n);
        }
        if (Math.min(u, v) < 1) {
            throw new IllegalArgumentException("EXCEPTION <<< u: " + u + " or v: " + v + " < 1");
        }

        // **** update root node (if needed) ****
        if (this.rootNode > Math.min(u, v)) {
            this.rootNode = Math.min(u, v);
        }

        // **** bidirectional links (v -> u and u -> v) ****
        this.adj.get(u).add(v);
        this.adj.get(v).add(u);
    }


    /**
     * Provided a set of vertices compute Kitty's calculation.
     */
    public int calc(int[] set) {

        // ???? ????
        System.out.println("calc <<<   set: " + Arrays.toString(set));

        // **** for starters ****
        int result  = 0;
        // int lca     = 0;
        // int u       = 0;
        // int v       = 0;

        // **** get the number of elements in the set ****
        int len = set.length;

        // **** check if done ****
        if (len == 1) {
            return 0;
        }

        // **** ****
        Combinations combinations = new Combinations();

        // **** generate array with combinations of n elements taken 2 at a time ****
        int[][] combs = combinations.genCombs(set);

        // **** display combinations ****
        System.out.println("calc <<< combs: " + Arrays.deepToString(combs));

        // **** loop once per combination of two vertices ****
        for (int i = 0; i < combs.length; i++) {

            // **** loop computing the distance between vertices ****
            // for (int j = 1; j < len; j++) {

                // **** set u ****
                int u = combs[i][0];

                // **** set v ****
                int v = combs[i][1];

                // **** determine lca between u and v ****
                int lca = naiveLCA(u, v);

                // ???? ????
                System.out.println("calc <<< lca: " + lca + " u: " + u + " v: " + v);

                // **** distance between u and lca ****
                int d = level[u] - level[lca];

                // **** distance between v and lca ****
                d += level[v] - level[lca];

                // ???? ????
                // System.out.println("calc <<< d: " + d);

                // **** ****
                d = u * v * d;

                // ???? ????
                System.out.println("calc <<< d: " + d);

                // **** ****
                result += d;
            // }
        }

        // ***** ****
        result %= MOD;

        // **** ****
        return result;
    }


    /**
     * Test scafolding
     */
    public static void main(String[] args) {
        
        // **** open the scanner ****
        Scanner sc = new Scanner(System.in);

        // **** read number of nodes (n) ****
        int n = sc.nextInt();

        // ???? ????
        System.out.println("main <<< n: " + n);

        // **** read the number of sets ****
        int q = sc.nextInt();

        // ???? ????
        System.out.println("main <<< q: " + q);

       // **** instantiate the object ****
       LowestCommonAncestor lca = new LowestCommonAncestor(n);

        // **** loop reading the edges ****
        for (int i = 0; i < (n - 1); i++) {

            // **** read edge nodes ****
            int u = sc.nextInt();
            int v = sc.nextInt();

            // ???? ????
            System.out.println("main <<< u: " + u + " v: " + v);

            // **** add this edge to the tree ****
            lca.addEdge(u, v);
        }

        // **** generate the Euler Tree ****
        lca.eulerTree(lca.getRootNode());

        // **** generate parent list ****
        lca.genParents();
     
        // **** clear the visited array (before populating level array) ****
        Arrays.fill(lca.vis, false);

        // **** generate the level array ****
        lca.genLevels(lca.getRootNode(), 1);

        // ???? ????
        System.out.println("main <<< lca\n" + lca.toString());
     
        // **** loop once per set of vertices ****
        for (int i = 0; i < q; i++) {

            // **** read the number of vertices (k) ****
            int k = sc.nextInt();

            // ???? ????
            System.out.println("main <<<   k: " + k);

            // **** holds the set of vertices ****
            int[] set = new int[k];

            // ***** read the set of vertices ****
            for (int j = 0; j < k; j++) {
                set[j] = sc.nextInt();
            }

            // ???? ????
            System.out.println("main <<< set: " + Arrays.toString(set));

            // **** ****
            int result = lca.calc(set);

            // **** display the result for the specified set ****
            System.out.println("main <<< result: " + result + "\n");
        }

        // **** close the scanner ****
        sc.close();
    }
}