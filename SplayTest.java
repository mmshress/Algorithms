import java.io.IOException;


class Node{

    int key;
    int size;

    public void setLeft(Node left) {
        this.left = left;
    }

    public void setRight(Node right) {
        this.right = right;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public Node getLeft() {
        return left;
    }

    public Node getRight() {
        return right;
    }

    public Node getParent() {
        return parent;
    }

    private Node left, right;
    private Node parent;

    public Node(int key){
        this.key = key;
        this.left = null;
        this.right = null;
        this.parent = null;
    }

}


class SplayTree {

    public static StringBuilder inorder;
    public Node root;
    public Node[] nodes;
    public Node[] nodesByKey;

    public void leftRotate(Node x){
        Node y = x.getRight();
        if(y != null){
            x.setRight(y.getLeft());
            if(y.getLeft() != null) y.getLeft().setParent(x);
            y.setParent(x.getParent());
        }

        if(x.getParent() == null) root = y;
        else if(x == x.getParent().getLeft()) x.getParent().setLeft(y);
        else x.getParent().setRight(y);

        if(y != null){
            y.setLeft(x);
        }
        x.setParent(y);
    }

    public void rightRotate(Node x){
        Node y = x.getLeft();
        if(y != null) {
            x.setLeft(y.getRight());
            if (y.getRight() != null) y.getRight().setParent(x);
            y.setParent(x.getParent());
        }
        if(x.getParent() == null) root = y;
        else if (x.getParent().getLeft() == x) x.getParent().setLeft(y);
        else x.getParent().setRight(y);

        if(y != null) y.setRight(x);
        x.setParent(y);
    }

    public void splay(Node x){
        if(x == null) return;
        while(x.getParent() != null){
            if(x.getParent().getParent() == null){
                if(x.getParent().getLeft() == x) rightRotate(x.getParent());
                else leftRotate(x.getParent());
            }
            else if(x.getParent().getLeft() == x && x.getParent().getParent().getLeft() == x.getParent()){
                rightRotate(x.getParent().getParent());
                rightRotate(x.getParent());
            }
            else if(x.getParent().getRight() == x && x.getParent().getParent().getRight() == x.getParent()){
                leftRotate(x.getParent().getParent());
                leftRotate(x.getParent());
            }
            else if(x.getParent().getLeft() == x && x.getParent().getParent().getRight() == x.getParent()){
                rightRotate(x.getParent());
                leftRotate(x.getParent());
            }
            else{
                leftRotate(x.getParent());
                rightRotate(x.getParent());
            }
        }
        computeSize(x);
    }



    public int getDepth(Node n){
        int depth = 0;
        while(n.getParent() != null){
            depth++;
            n = n.getParent();
        }
        return depth;
    }

    public SplayTree(int n, int[] array){
        inorder = new StringBuilder();
         Node currentParent = new Node(array[n - 1]);
         nodes = new Node[n];
         nodesByKey = new Node[n];
        this.root = currentParent;
        nodesByKey[array[n - 1]] = currentParent;
        nodes[n - 1] = currentParent;
        for(int i = n - 2; i >= 0; i--){
            Node node = new Node(array[i]);
            nodesByKey[array[i]] = node;
            nodes[i] = node;
            currentParent.setLeft(node);
            node.setParent(currentParent);
            currentParent = node;
        }
        computeSize(this.root);
    }

    public SplayTree(Node root){
        this.root = root;

    }

    public void traverse(Node current, int depth){
        if(depth == 0) inorder = new StringBuilder();
        if(current.getLeft() != null){
            traverse(current.getLeft(), depth + 1);
        }
        inorder.append(current.key);
        inorder.append(", ");
        if(current.getRight() != null){
            traverse(current.getRight(), depth + 1);
        }
    }

    public void computeSize(Node n){
        if(n != null){
            n.size = 1;
        }
        if(n.getRight() != null){
            computeSize(n.getRight());
            n.size += n.getRight().size;
        }
        if(n.getLeft() != null){
            computeSize(n.getLeft());
            n.size += n.getLeft().size;
        }
    }
}

public class SplayTest{
    public static void main(String[] args) throws IOException {
//        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//        String[] line = reader.readLine().trim().split(" ");
        int n = 6;
        int[] array = {0, 1, 2, 3, 4, 5};

        SplayTree tree = new SplayTree(n, array);
        tree.traverse(tree.root, 0);
        tree.splay(tree.root.getLeft());
        tree.traverse(tree.root, 0);
    }
}