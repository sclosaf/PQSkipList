// Sclosa Fabrizio, n. matricola: 2101826

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

// class MyEntry used to manage the entries inserted in the skip list
class MyEntry 
{       
    // Constructor
    public MyEntry(Integer key, String value) 
    {
        this.key = key;
        this.value = value;
    }

    // Getter method for the key
    public Integer getKey() 
    {return key;}
    
    // Getter method for the value
    public String getValue() 
    {return value;}
    
    // Method that prints the entry with the format: "key value"
    public String toString() 
    {return key + " " + value;}
    
    // private members
    private Integer key;
    private String value;
}

// class Node used to organize entries and the links between them, accordingly to the skip list principles
class Node 
{
    // Constructor
    public Node(MyEntry entry) throws IllegalNodeException
    {
        // Check if the entry inserted is valid, if not throws IllegalNodeException
        if(entry == null)
            throw new IllegalNodeException("Error: created node with invalid entry");

        // Members initialization
        this.entry = entry;

        nextLinkedNode = new ArrayList<>();
        prevLinkedNode = new ArrayList<>();
    }

    // Getter method for the entry
    public MyEntry getEntry()
    {return entry;}

    // Returns the value of the highest level of the next node linked
    public int topLevelRight()
    {return nextLinkedNode.size() - 1;}    

    // Returns the value of the highest level of the previous node linked
    public int topLevelLeft()
    {return prevLinkedNode.size() - 1;}

    // Method that checks if the two lists of the node have the same height
    public boolean isBalanced()
    {return topLevelLeft() == topLevelRight();}

    // Returns the number of levels that the node has
    // If the lists of the node are not balanced throws IllegalNodeException
    public int size() throws IllegalNodeException
    {
        if(!isBalanced())
            throw new IllegalNodeException("Error: the node is not balanced, right and left level don't correspond.");
    
        return prevLinkedNode.size();
    }

    // Returns the highest level of the node
    // If the lists are not balanced throws IllegalNodeException
    public int topLevel() throws IllegalNodeException
    {
        if(!isBalanced())
            throw new IllegalNodeException("Error: the node is not balanced, right and left level don't correspond.");
    
        return topLevelLeft();
    }

    // Method that checks if "this" and "node" have the same height
    public boolean sameTopLevel(Node node)
    {return this.topLevel() == node.topLevel();}

    // Getter method for the next linked node at the i-th level
    // Check for the value of i, if out of range throws IllegalNodeException
    public Node getNextAt(int i) throws IndexOutOfBoundsException
    {
        if(i < 0 || i >= nextLinkedNode.size())
            throw new IndexOutOfBoundsException("Error: index out of bound.");
        
        return nextLinkedNode.get(i);
    }

    // Getter method for the previous linked node at the i-th level
    // Check for the value of i, if out of range throws IllegalNodeException
    public Node getPrevAt(int i) throws IndexOutOfBoundsException
    {
        if(i < 0 || i >= prevLinkedNode.size())
            throw new IndexOutOfBoundsException("Error: index out of bound.");

        return prevLinkedNode.get(i);
    }

    // Setter method for the next linked node at the i-th level
    // Check for the value of i, if out of range throws IllegalNodeException
    public void setNextAt(int i, Node node) throws IndexOutOfBoundsException
    {
        if(i < 0 || i >= nextLinkedNode.size())
            throw new IndexOutOfBoundsException("Error: index out of bound.");

        nextLinkedNode.set(i, node);
    }

    // Setter method for the previous linked node at the i-th level
    // Check for the value of i, if out of range throws IllegalNodeException
    public void setPrevAt(int i, Node node) throws IndexOutOfBoundsException
    {
        if(i < 0 || i >= prevLinkedNode.size())
            throw new IndexOutOfBoundsException("Error: index out of bound.");

        prevLinkedNode.set(i, node);
}

    // Method that adds a node in the list of next linked nodes
    public void addNext(Node node)
    {nextLinkedNode.add(node);}

    // Method that adds a node in the list of previous linked nodes
    public void addPrev(Node node)
    {prevLinkedNode.add(node);}

    // Method that removes the link to the next linked node at the i-th level
    // Check for the value of i, if out of range throws IllegalNodeException
    public void removeNextAt(int i) throws IndexOutOfBoundsException
    {
        if(i < 0 || i >= nextLinkedNode.size())
            throw new IndexOutOfBoundsException("Error: index out of bound.");

        nextLinkedNode.remove(i);
    }

    // Method that removes the link to the previous linked node at the i-th level
    // Check for the value of i, if out of range throws IllegalNodeException
    public void removePrevAt(int i) throws IndexOutOfBoundsException
    {
        if(i < 0 || i >= prevLinkedNode.size())
            throw new IndexOutOfBoundsException("Error: index out of bound.");
        
        prevLinkedNode.remove(i);
    } 

    // Resets the node, empties the relative lists and invalidates the entry
    public void resetNode()
    {
        nextLinkedNode.clear();
        prevLinkedNode.clear();

        nextLinkedNode.trimToSize();
        prevLinkedNode.trimToSize();

        entry = null;
    }

    // Private Members
    private MyEntry entry;// Entry associated to the node

    // Lists associated to each entry, used to manage and control the linking between the nodes
    // The reference to the node at the i-th index rappresents the link to the node at the corresponding level (the i-th level)
    private ArrayList<Node> nextLinkedNode; // List of links to the next nodes
    private ArrayList<Node> prevLinkedNode; // List of links to the previous nodes
}

class SkipListPQ 
{
    // Constructor
    public SkipListPQ(double alpha) 
    {
        // Members initialization
        this.alpha = alpha;
        this.rand = new Random();

        size = 0;
        height = 0;

        // Initialization of the sentinels and rise up to the value of height
        leftSentinel = new Node(new MyEntry(Integer.MIN_VALUE, null));
        rightSentinel = new Node(new MyEntry(Integer.MAX_VALUE, null));
    
        resizeSentinels(1);
    }

    // Returns the number of entries currently present in the skip list
    public int size() 
    {return size;}


    // Method that prints the entry with minimum key and returns it
    // If the skip list is empty returns null
    public MyEntry min() 
    {
        if(isEmpty())
            return null;

        System.out.println(leftSentinel.getNextAt(0).getEntry().toString());

        return leftSentinel.getNextAt(0).getEntry();
    }

    // Method that inserts a new entry into the skip list
    // Returns the number of traversed nodes from the highest node of the leftSentinel to the previous node in which the new entry will be inserted
    public int insert(int key, String value) throws IllegalSentinelStructureException
    {
        // Generating the number of promotions for the new entry
        int level = generateEll(alpha, key);

        // Check if the sentinel are able to contain the new entry with its promotions
        // If not, sentinels will be rised to make the new entry fit properly, leaving the last level empty, with just the two sentinels
        resizeSentinels(level + 1);
    
        int[] countOperations = {0};

        Node[] searchedPreviousNodes = search(key, countOperations, level + 1);

        Node newNode = new Node(new MyEntry(key, value));

        // Linking the new node with all the previous and the next nodes, at the i-th level
        for(int i = 0; i <= level; i++)
        {
            Node previousNode = searchedPreviousNodes[i];
            Node nextNode = previousNode.getNextAt(i);
 
            newNode.addPrev(previousNode);
            newNode.addNext(nextNode);

            nextNode.setPrevAt(i, newNode);
            previousNode.setNextAt(i, newNode);
        }

        // Increments the number of entries in the skip list
        size++;

        return countOperations[0];
    }

    // Helper function that generates the number of promotions to a superior level that an inserted entry will recive
    // The generation principle is based on the value of alpha_
    private int generateEll(double alpha_, int key) 
    {
        int level = 0;
        
        if (alpha_ >= 0. && alpha_< 1)
            while (rand.nextDouble() < alpha_)
                level++;
        else
            while (key != 0 && key % 2 == 0)
            {
                key /= 2;
                level++;
            }
        
        return level;
    }

    // Method that removes the entry with minimum key from the skip list and links the previous and next nodes of the one removed
    // Resets the removed node and remove the excessive levels of the sentinels if there are over the current highest promoted node 
    // Returns the entry contained into the removed node
    // Throws EmptySkipListException if the skip list is empty
    public MyEntry removeMin() throws EmptySkipListException
    {
        if(isEmpty()) 
            throw new EmptySkipListException("Error: the skip list is empty, impossible to remove the minimum element.");

        Node currentNode = leftSentinel.getNextAt(0);
        MyEntry entry = currentNode.getEntry();

        // Links the previous and next nodes of the removed one
        for(int i = 0; i <= currentNode.topLevel(); i++)
        {
            currentNode.getPrevAt(i).setNextAt(i, currentNode.getNextAt(i));
            currentNode.getNextAt(i).setPrevAt(i, currentNode.getPrevAt(i));
        }
        
        currentNode.resetNode();

        // Check and, eventually, removes the levels of sentinels in excess
        resizeSentinels(height);

        // Decrement of the number of entries in the skip list
        size--;

        return entry;
    }

    // Method that prints the whole skip list, in particular the related entry and the number of promotions of each node
    // Each print of the datas of a node is separated by a comma
    public void print() 
    {
        if(isEmpty())
            return;

        Node currentNode = leftSentinel.getNextAt(0);

        while(currentNode != rightSentinel)
        {            
            System.out.print(currentNode.getEntry().toString() + " " + currentNode.size());
            
            currentNode = currentNode.getNextAt(0);

            if(currentNode != rightSentinel)
                System.out.print(", ");
            else
                System.out.print("\n");
        }
    }

    // Helper function that searchs the node in the skip that has a lower or equal key of the searched key
    // Returns an array of nodes where the i-th index contains the previous node at the corrisponding level (i-th) of the searched key
    // This lets to have access to the nodes that have a key set previously the new node, at all the levels 
    // The search starts from the highest node of the left sentinel and confronts the searched key with the next node linked
    // If its key is lower or equal, the process continues from the next node, if it is higher the process continues from the same node at the level below
    // The function counts the number of nodes traversed during this operation and saves this counting in the first index of the array
    // The array is used only for the purpose to simulate the pass by reference (that java doesn't support)
    private Node[] search(int key, int[] arrayHelper, int numberOfNodesSearched)
    {
        // Creates a proper array that will be returned
        Node[] searchedNodes = new Node[numberOfNodesSearched];
        
        Node calculatedNode = leftSentinel;
        
        for(int i = height; i >= 0; i--, arrayHelper[0]++)
        {
            // Check that searches the node with lower or equal key
            // Which also grants that eventual entries with key equal to the key of the rightSentinel are setted before it
            while(calculatedNode.getNextAt(i) != rightSentinel && calculatedNode.getNextAt(i).getEntry().getKey() <= key)
            {
                calculatedNode = calculatedNode.getNextAt(i);
                arrayHelper[0]++;                
            }

            // Adds the previous nodes, of the searched key, to the list at the corresponding level (i-th)
            if(i < numberOfNodesSearched)
                searchedNodes[i] = calculatedNode;
        }
        
        return searchedNodes;
    }

    // Method that checks if the skip list is currently empty
    public boolean isEmpty()
    {return size == 0;}

    // Getter for the value height
    public int getHeight()
    {return height;}

    // Helper function that manages the sentinels and their height, if the parameter passed is higher of the current height it raises them to the new value
    // Else if the value is equal or lower the function just checks if there are unnecessary levels of sentinels and removes them
    // Throws IllegalSentinelStructureException if the sentinels don't have the same height
    private void resizeSentinels(int newHeight) throws IllegalSentinelStructureException
    {
        if(!leftSentinel.sameTopLevel(rightSentinel))
            throw new IllegalSentinelStructureException("Error: the sentinels are not balanced with each other.");
        
        if(newHeight > height)
        {
            height = newHeight;

            for(int i = leftSentinel.topLevel(); i <= height; i++)
            {
                leftSentinel.addNext(rightSentinel);
                leftSentinel.addPrev(null);

                rightSentinel.addNext(null);
                rightSentinel.addPrev(leftSentinel);
            }
        }
        else 
        {
            while(height > 1 && leftSentinel.getNextAt(height - 1) == rightSentinel)
            {
                leftSentinel.removeNextAt(height);
                leftSentinel.removePrevAt(height);
                
                rightSentinel.removePrevAt(height);
                rightSentinel.removeNextAt(height);

                height--;
            }
        }
    }

    // Debugging method used during the implementation and the realization of the class SkipListPQ
    // Mainly used to check proper nodes linking and relative insertion in the skip list
    // Prints the entries of a node and the corresponding links for each level of the skip list
    public void debug() 
    {
    
        System.out.println("\n========== Debugging Skip List ==========");


        for(int i = height; i >= 0; i--)
        {
            System.out.print("\nLevel S" + i + ": ");
            
            Node currentNode = leftSentinel;
            String nodeInfo = "";

            while (currentNode != null) 
            {
                if(currentNode == leftSentinel)
                {
                    nodeInfo += "-inf";
                    
                    nodeInfo += " ";

                    if(currentNode.getNextAt(i).getPrevAt(i) == currentNode)
                        nodeInfo += "< - ";
                }
                else if(currentNode == rightSentinel)
                {
                    if(currentNode.getPrevAt(i).getNextAt(i) == currentNode)
                        nodeInfo += ">";

                    nodeInfo += " ";

                    nodeInfo += "+inf";
                }
                else 
                {
                    if(currentNode.getPrevAt(i).getNextAt(i) == currentNode)
                        nodeInfo += ">";
                    
                    nodeInfo += " ";

                    nodeInfo += currentNode.getEntry().toString();

                    nodeInfo += " ";

                    if(currentNode.getNextAt(i).getPrevAt(i) == currentNode)
                        nodeInfo += "<";

                    nodeInfo += " - ";
                }

                currentNode = currentNode.getNextAt(i);
            }
            
            System.out.println(nodeInfo);
        }

        System.out.println("\n========== End Debugging ==========\n");
    }

    // Private members
    private double alpha; // Variable that defines the probability for the promotion of an entry
                          // If the value is out of the range [0,1) the skip list will use a deterministic function for the promotion

    private Random rand; // Variable used for the generation of a random number that will determinate if an inserted entry will be promoted
                         // Simulates the toss of a coin

    private int height; // Variable used to memorize the last level that the skip list currently has
                        // Which contains only the two sentinels, assumes values that are in the range [1, +inf)
                        // When the skip list is empty, it is setted at 1
    
    private int size; // Variable used to count the number of entries currently present in the skip list
                      // The two sentinels are not considered in this counting

    private Node leftSentinel; // Sentinel of the skip list, initialized with the entry (-inf, null)
                               // Used for the management of the skiplist and its boundaries
    
    private Node rightSentinel; // Sentinel of the skip list, initialized with the entry (+inf, null)
                                // Used for the management of the skiplist and its boundaries
}

// Classes that manage the exceptions that may occur in the class SkipListPQ
// With the override of the costructor in order to manage eventual messages
class IllegalSentinelStructureException extends RuntimeException
{
    public IllegalSentinelStructureException(String message) 
    {super(message);}
}

class IllegalNodeException extends RuntimeException
{
    public IllegalNodeException(String message)
    {super(message);}
}

class EmptySkipListException extends RuntimeException
{
    public EmptySkipListException(String message)
    {super(message);}
}

public class TestProgram 
{
    public static void main(String[] args) 
    {
        if (args.length != 1) 
        {
            System.out.println("Usage: java TestProgram <file_path>");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(args[0]))) 
        {
            String[] firstLine = br.readLine().split(" ");
            int N = Integer.parseInt(firstLine[0]);
            double alpha = Double.parseDouble(firstLine[1]);
            System.out.println(N + " " + alpha);

            SkipListPQ skipList = new SkipListPQ(alpha);

            long executedInserts = 0; // Counter for the number of insert occurred
            long traversedNodes = 0; // Counter for the total number of nodes traversed during the whole execution of the programm

            for (int i = 0; i < N; i++) {
                String[] line = br.readLine().split(" ");
                int operation = Integer.parseInt(line[0]);

                switch (operation) 
                {
                    case 0:
		            	skipList.min();
                        break;
                    case 1:
			            skipList.removeMin(); 
                        break;
                    case 2:
                        traversedNodes += skipList.insert(Integer.parseInt(line[1]), line[2]);
                        executedInserts++;
                        break;
                    case 3:
			            skipList.print();
                        break;
                    default:
                        return;
                }
            }

            System.out.println(alpha + " " + skipList.size() + " " + executedInserts + " " + traversedNodes/(double)executedInserts);
        } 
        catch(IOException e){System.out.println("Error reading file: " + e.getMessage());}
        catch(IllegalSentinelStructureException e){System.out.println(e.getMessage());}
        catch(IllegalNodeException e){System.out.println(e.getMessage());}
        catch(EmptySkipListException e){System.out.println(e.getMessage());}
        catch(IndexOutOfBoundsException e){System.out.println(e.getMessage());}
    }
}