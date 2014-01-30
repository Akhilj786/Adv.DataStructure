
import java.util.*; // For ArrayList

/**
 * A class representing a f-heap scheme.
 *
 * @param T The type of elements to store in the heap.
 * @author Akhil Jain
 */
public final class Fheap<T> {
    
    /* Pointer to the minimum element in the heap. */
    private Node<T> mMin = null;
    private int mSize = 0;

    /**
     * Inserts the specified element into the Fibonacci heap with the specified
     * priority.  
     */
    public Node<T> enQueue(T value, double priority) {
        checkPriority(priority);
        Node<T> result = new Node<T>(value, priority);
        mMin = mergeLists(mMin, result);
        ++mSize;
        return result;
    }
    
    /**
     * Returns whether the heap is empty.
     *
     * @return Whether the heap is empty.
     */
    public boolean isEmpty() {
        return mMin == null;
    }
    
    /**
     * Returns an Entry object corresponding to the minimum element of the
     * Fibonacci heap.
     */
    public Node<T> min() {
        if (isEmpty())
            throw new NoSuchElementException("Heap is empty.");
        return mMin;
    }
    
    /**
     * Returns the number of elements in the heap.
     *
     * @return The number of elements in the heap.
     */
    public int size() {
        return mSize;
    }

    /**
     *Merges two heap
     */
    public static <T> Fheap<T> merge(Fheap<T> one, Fheap<T> two) {
        /* Create a new FibonacciHeap to hold the result. */
        Fheap<T> result = new Fheap<T>();

        /* Merge the two Fibonacci heap root lists together.  This helper function
         * also computes the min of the two lists, so we can store the result in
         * the mMin field of the new heap.
         */
        result.mMin = mergeLists(one.mMin, two.mMin);

        /* The size of the new heap is the sum of the sizes of the input heaps. */
        result.mSize = one.mSize + two.mSize;

        /* Clear the old heaps. */
        one.mSize = two.mSize = 0;
        one.mMin  = null;
        two.mMin  = null;

        /* Return the newly-merged heap. */
        return result;
    }

    /**
     * Dequeues and returns the minimum element of the Fibonacci heap. 
     */
    public Node<T> dequeueMin() {
        /* Check for whether we're empty. */
        if (isEmpty())
            throw new NoSuchElementException("Heap is empty.");

        --mSize;

        /* Grab the minimum element so we know what to return. */
        Node<T> minElem = mMin;

        /*Two case to remove minimum element.First, this is last element in list of roots 
         * then we set list of roots to be null,otherwise we write the elements next to the
         * min element around the min element to remove it, then arbitrarily
         * reassign the min.
         */
        if (mMin.mNext == mMin) { // Case one
            mMin = null;
        }
        else { // Case two
            mMin.mPrev.mNext = mMin.mNext;
            mMin.mNext.mPrev = mMin.mPrev;
            mMin = mMin.mNext; // Arbitrary element of the root list.
        }
        
        if (minElem.mChild != null) {
            /* Keep track of the first visited node. */
            Node<?> curr = minElem.mChild;
            do {
                curr.mParent = null;
                curr = curr.mNext;
            } while (curr != minElem.mChild);
        }

        mMin = mergeLists(mMin, minElem.mChild);
        if (mMin == null) return minElem;

        List<Node<T>> treeTable = new ArrayList<Node<T>>();
        List<Node<T>> toVisit = new ArrayList<Node<T>>();
        for (Node<T> curr = mMin; toVisit.isEmpty() || toVisit.get(0) != curr; curr = curr.mNext)
            toVisit.add(curr);
        for (Node<T> curr: toVisit) {
            while (true) {
                while (curr.mDegree >= treeTable.size())
                    treeTable.add(null);

                if (treeTable.get(curr.mDegree) == null) {
                    treeTable.set(curr.mDegree, curr);
                    break;
                }

                /* Otherwise, merge with what's there. */
                Node<T> other = treeTable.get(curr.mDegree);
                treeTable.set(curr.mDegree, null); // Clear the slot

                Node<T> min = (other.mPriority < curr.mPriority)? other : curr;
                Node<T> max = (other.mPriority < curr.mPriority)? curr  : other;

                max.mNext.mPrev = max.mPrev;
                max.mPrev.mNext = max.mNext;

                /* Make it a singleton so that we can merge it. */
                max.mNext = max.mPrev = max;
                min.mChild = mergeLists(min.mChild, max);
                
                /* Reparent max appropriately. */
                max.mParent = min;

                /* Clear max's mark, since it can now lose another child. */
                max.mIsMarked = false;

                /* Increase min's degree; it now has another child. */
                ++min.mDegree;

                /* Continue merging this tree. */
                curr = min;
            }

            /* Update the global min based on this node.  
             */
            if (curr.mPriority <= mMin.mPriority) mMin = curr;
        }
        return minElem;
    }

    /**
     * Decreases the key of the specified element to the new priority.  
     */
    public void decreaseKey(Node<T> entry, double newPriority) {
        checkPriority(newPriority);
        if (newPriority > entry.mPriority)
            throw new IllegalArgumentException("New priority exceeds old.");

        /* Forward this to a helper function. */
        decreaseKeyUnchecked(entry, newPriority);
    }
    
    /**
     * Deletes this Entry from the Fibonacci heap that contains it.
     *
     * It is assumed that the entry belongs in this heap.  For efficiency
     * reasons, this is not checked at runtime.
     *
     * @param entry The entry to delete.
     */
    public void delete(Node<T> entry) {
        decreaseKeyUnchecked(entry, Double.NEGATIVE_INFINITY);
        dequeueMin();
    }

    /**
     * Utility function which, given a user-specified priority, checks whether
     * it's a valid double and throws an IllegalArgumentException otherwise.
    
     */
    private void checkPriority(double priority) {
        if (Double.isNaN(priority))
            throw new IllegalArgumentException(priority + " is invalid.");
    }

    /**
     * Utility function which, given two pointers into disjoint circularly-
     * linked lists, merges the two lists together into one circularly-linked
     * list 
     */
    private static <T> Node<T> mergeLists(Node<T> one, Node<T> two) {
        /* There are four cases depending on whether the lists are null or not.
         * We consider each separately.
         */
        if (one == null && two == null) { // Both null, resulting list is null.
            return null;
        }
        else if (one != null && two == null) { // Two is null, result is one.
            return one;
        }
        else if (one == null && two != null) { // One is null, result is two.
            return two;
        }
        else { // Both non-null; actually do the splice.
           
            Node<T> oneNext = one.mNext; // Cache this since we're about to overwrite it.
            one.mNext = two.mNext;
            one.mNext.mPrev = one;
            two.mNext = oneNext;
            two.mNext.mPrev = two;

            /* Return a pointer to whichever's smaller. */
            return one.mPriority < two.mPriority? one : two;
        }
    }

    /**
     * Decreases the key of a node in the tree without doing any checking to ensure
     * that the new priority is valid.
     */
    private void decreaseKeyUnchecked(Node<T> entry, double priority) {
        /* First, change the node's priority. */
        entry.mPriority = priority;

        if (entry.mParent != null && entry.mPriority <= entry.mParent.mPriority)
            cutNode(entry);
        if (entry.mPriority <= mMin.mPriority)
            mMin = entry;
    }

    /**
     * Cuts a node from its parent.  If the parent was already marked, recursively
     * cuts that node from its parent as well.
     */
    private void cutNode(Node<T> entry) {
        /* Begin by clearing the node's mark, since we just cut it. */
        entry.mIsMarked = false;

        /* Base case: If the node has no parent, we're done. */
        if (entry.mParent == null) return;

        /* Rewire the node's siblings around it, if it has any siblings. */
        if (entry.mNext != entry) { // Has siblings
            entry.mNext.mPrev = entry.mPrev;
            entry.mPrev.mNext = entry.mNext;
        }

        /* If the node is the one identified by its parent as its child,
         * we need to rewrite that pointer to point to some arbitrary other
         * child.
         */
        if (entry.mParent.mChild == entry) {
            /* If there are any other children, pick one of them arbitrarily. */
            if (entry.mNext != entry) {
                entry.mParent.mChild = entry.mNext;
            }
            /* Otherwise, there aren't any children left and we should clear the
             * pointer and drop the node's degree.
             */
            else {
                entry.mParent.mChild = null;
            }
        }

        /* Decrease the degree of the parent, since it just lost a child. */
        --entry.mParent.mDegree;

        /* Splice this tree into the root list by converting it to a singleton
         * and invoking the merge subroutine.
         */
        entry.mPrev = entry.mNext = entry;
        mMin = mergeLists(mMin, entry);

        /* Mark the parent and recursively cut it if it's already been
         * marked.
         */
        if (entry.mParent.mIsMarked)
            cutNode(entry.mParent);
        else
            entry.mParent.mIsMarked = true;

        /* Clear the relocated node's parent; it's now a root. */
        entry.mParent = null;
    }
}