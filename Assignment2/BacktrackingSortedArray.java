
public class BacktrackingSortedArray implements Array<Integer>, Backtrack {
    private Stack stack;
    private int[] arr;
    private int nextPlace; // pointer to the next available cell in the array


    // Do not change the constructor's signature
    public BacktrackingSortedArray(Stack stack, int size) {
        this.stack = stack;
        arr = new int[size];
        nextPlace = 0;
    }

    @Override
    public Integer get(int index) {  // O(1)
        if (index >= 0 & index < nextPlace)
            return arr[index];
        return null;
    }

    @Override
    public Integer search(int x) { // O(log(n)) - binary search
        int low = 0;
        int high = nextPlace - 1;
        while (low <= high) {
            int mid = (low + high) / 2;
            if (arr[mid] == x)
                return mid;
            else if (x < arr[mid])
                high = mid - 1;
            else
                low = mid + 1;
        }
        return -1;
    }

    @Override
    public void insert(Integer x) {
        // in every 'insert'/'delete' operation, we create a new object with the next fields:
        // x- value inserted/deleted
        // y- the index that the value was insert to or delete from
        // z- '1' that means the last performed operation was 'insert' or '0' that means the last performed operation was 'delete'
        // than, we push it to the stack.
        if (x != null) {
            int indexInserted = 0;
            boolean gotInto = false;
            if (nextPlace == 0) {
                arr[nextPlace] = x;
                nextPlace = nextPlace + 1;
            } else if (rangeCheck()) {
                arr[nextPlace] = x;
                for (int i = nextPlace; i > 0; i--) {
                    if (arr[i - 1] > arr[i]) {
                        swap(i, i - 1);
                        indexInserted = i - 1;
                        gotInto = true;
                    } else if (!gotInto) indexInserted = nextPlace;
                }
                nextPlace = nextPlace + 1;
            }
            stack.push(new Memo(x, indexInserted, 1)); // x- value inserted
            // y- the index that the value was insert to
            // z- '1' - the last performed operation was 'insert'
        }
    }

    private void swap(int i, int j) {
        // this function used in the insertion
        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }

    private boolean rangeCheck() {
        return nextPlace >= 0 & nextPlace < arr.length;
        // O(1)
        // this function checks if we are out from array's bounds

    }


    @Override
    public void delete(Integer index) {// O(n)
        if (index < nextPlace) {
            // x- value deleted
            // y- the index that the value was delete from
            // z- '0' - the last performed operation was 'delete'
            stack.push(new Memo(arr[index], index, 0));
            for (int i = index; i < nextPlace - 1; i++) {
                arr[i] = arr[i + 1];
            }
            nextPlace = nextPlace - 1;
        }
    }


    @Override
    public Integer minimum() { // O(1)
        if (nextPlace == 0)
            return -1;
        return 0;
    }

    @Override
    public Integer maximum() {  // O(1)
        if (nextPlace == 0)
            return -1;
        return nextPlace - 1;
    }

    @Override
    public Integer successor(Integer index) { // O(1)
        if (index < 0 | index > nextPlace | (index == nextPlace - 1) | index == nextPlace)
            return -1;
        return index + 1;
    }

    @Override
    public Integer predecessor(Integer index) { // O(1)
        if (index <= 0 | (index >= nextPlace + 1))
            return -1;
        return index - 1;
    }

    @Override
    public void backtrack() {  // O(n)
        if (!stack.isEmpty()) {
            Memo memo = (Memo) stack.pop();  // we pulled out from the stack the object that holds the relevant pointers
            // so we can go back to the back state of the array
            if (memo.getZ() == 1) { // means that the last performed operation was 'insert'
                for (int i = memo.getY(); i < nextPlace - 1; i++) { // than we need to delete this number by moving all the numbers
                    // in the array one step left starting in the index that appear in the field y of the object that we pulled out from
                    // the stack
                    arr[i] = arr[i + 1];
                }
                nextPlace = nextPlace - 1;
            } else { // means that the last performed operation was 'delete'
                arr[nextPlace] = memo.getX();
                for (int i = nextPlace; i > 0; i--) { // than we need to insert the value that we removed and locate it
                    // in the same cell it was before we delete it
                    if (arr[i - 1] > arr[i])
                        swap(i, i - 1);
                }
                nextPlace = nextPlace + 1;
            }
        }
        System.out.print("backtracking performed");

    }


    @Override
    public void retrack() {
        // Do not implement anything here!!
    }

    @Override
    public void print() {  // O(n)
        String output = "";
        if (nextPlace > 0) {
            for (int i = 0; i < nextPlace; i++) {
                output = output + arr[i] + " ";
            }
            output = output.substring(0, output.length() - 1);
            System.out.print(output);
        }
    }

}
