
public class BacktrackingArray implements Array<Integer>, Backtrack {
    private Stack stack;
    private int[] arr;
    private int nextPlace; // pointer to the next available cell in the array

    // TODO: implement your code here

    // Do not change the constructor's signature
    public BacktrackingArray(Stack stack, int size) {
        this.stack = stack;
        arr = new int[size];
        nextPlace = 0;
    }

    @Override
    public Integer get(int index) { // O(1)
        if (index >= 0 & index < nextPlace)
            return arr[index];
        return null;
    }

    @Override
    public Integer search(int x) { // O(n)
        if (arr.length != 0) {
            for (int i = 0; i < nextPlace; i++) {
                if (arr[i] == x)
                    return i;
            }
        }
        return -1;
    }
    // in every 'insert'/'delete' operation, we create a new object with the next fields:
    // x- value inserted/deleted
    // y- the index that the value was insert to or delete from
    // z- the pointer to the next available cell in the array before the modifying (before the 'insert' operation
    // or the 'delete' operation)
    // than, we push it to the stack.
    @Override
    public void insert(Integer x) {
        if (rangeCheck() & (x != null)) {
            stack.push(new Memo(arr[nextPlace], nextPlace, nextPlace));
            arr[nextPlace] = x;
            nextPlace = nextPlace + 1;
        }
    }

    private boolean rangeCheck() { // O(1)
        return nextPlace >= 0 & nextPlace < arr.length;
    }

    @Override // O(1)
    public void delete(Integer index) {
        if (index != null && (index >= 0 & index < nextPlace)) {
            // x- value deleted
            // y- the index that the value was delete from
            // z- the pointer to the next available cell in the array before the modifying (before the 'delete' operation)
            stack.push(new Memo(arr[index], index, nextPlace));
            nextPlace = nextPlace - 1;
            arr[index] = arr[nextPlace];
        }
    }

    @Override
    public Integer minimum() {  // O(n)
        if (nextPlace == 0)
            return -1;
        int minValue = arr[0];
        for (int i = 1; i < nextPlace; i++) {
            if (arr[i] < minValue)
                minValue = arr[i];
        }
        return minValue;
    }

    @Override
    public Integer maximum() { // O(n)
        if (arr.length == 0)
            return -1;
        int maxValue = arr[0];
        for (int i = 1; i < nextPlace; i++) {
            if (arr[i] > maxValue)
                maxValue = arr[i];
        }
        return maxValue;
    }

    @Override
    public Integer successor(Integer index) {  // O(n)
        // the successor of x is the minimum value between all of the values that bigger than x,
        // so once we found a bigger value than x, we saved it and when we will find another value
        // that bigger than x, we compare it with the first value that we saved at the beginning, and keep on doing that
        // till the end of the array.
        int suc = -1;
        boolean ok = false;
        if (index >= 0 && index < nextPlace) {
            for (int i = 0; i < nextPlace; i++) {
                if (suc == -1 && arr[index] < arr[i]) {
                    suc = i;
                    ok = true;
                } else {
                    if (ok && arr[i] < arr[suc] && arr[i] > arr[index]) {
                        suc = i;
                    }
                }
            }
        }
        return suc;
    }

    @Override
    public Integer predecessor(Integer index) { // O(n)
        // the predecessor of x is the maximum value between all of the values that smaller than x,
        // so once we found a smaller value than x, we saved it and when we will find another value
        // that smaller than x, we compare it with the first value that we saved at the beginning, and keep on doing that
        // till the end of the array.
        int pre = -1;
        boolean ok = false;
        if (index >= 0 && index < nextPlace) {
            for (int i = 0; i < nextPlace; i++) {
                if (pre == -1 && arr[index] > arr[i]) {
                    pre = i;
                    ok = true;
                } else {
                    if (ok && arr[i] > arr[pre] && arr[i] < arr[index]) {
                        pre = i;
                    }
                }
            }
        }
        return pre;
    }

    @Override
    public void backtrack() { // O(1)
        // in every backtrack operation, we pulled out from the stack an object that holds fields which use as a relevant pointers and
        // by that we can to return the array to the prior state
        if (!stack.isEmpty()) {
            Memo memo = (Memo) stack.pop();
            arr[memo.getY()] = memo.getX();
            nextPlace = memo.getZ();
            System.out.print("backtracking performed");
        }
    }

    @Override
    public void retrack() {
        // Do not implement anything here!!
    }

    @Override
    public void print() { // O(n)
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

