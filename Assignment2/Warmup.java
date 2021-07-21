public class Warmup {
    public static int backtrackingSearch(int[] arr, int x, int fd, int bk, Stack myStack) {
        int i = 0;
        int fd1 = fd;
        int bk1 = bk;
        while (i < arr.length) { // as long as we are in the array's bounds
            while (fd1 != 0 && i < arr.length) { // fd search steps
                if (arr[i] == x)
                    return i;
                else {
                    myStack.push(i); //save the index for memory to backtrack
                    fd1 = fd1 - 1;
                    i = i + 1;
                }
            }
            while (bk1 != 0 && i < arr.length) { // backtrack and edit the index
                if (!myStack.isEmpty()) {
                    bk1 = bk1 - 1;
                    i = (int) myStack.pop();
                }
            }
            fd1 = fd;
            bk1 = bk;
        }
        return -1;
    }

    public static int consistentBinSearch(int[] arr, int x, Stack myStack) {
        Memo memo = new Memo(0, (arr.length - 1) / 2, arr.length - 1); // memo(low,middle,high)
        while (memo.getX() <= memo.getZ()) { //while (low <= high)
            int isCon = isConsistent(arr);
            if (isCon != 0) { //amount of backtracking times
                for (int i = 0; i < isCon; i++) {
                    if (!myStack.isEmpty()) {
                        memo = (Memo) myStack.pop();
                    }
                }
            } else {
                memo.setY((memo.getX() + memo.getZ()) / 2); //set middle = (low+high)/2
                if (arr[memo.getY()] == x) { // if arr[middle = x]
                    return memo.getY(); //return middle (index)
                } else if (x < arr[memo.getY()]) { //else if ( x < arr[middle] )
                    myStack.push(new Memo(memo.getX(), memo.getY(), memo.getZ())); //push memo(low, middle, high)
                    memo.setZ(memo.getY() - 1); // high = middle -1;
                } else {
                    myStack.push(new Memo(memo.getX(), memo.getY(), memo.getZ())); // //push memo(low, middle, high)
                    memo.setX(memo.getY() + 1); // low = middle +1;
                }
            }
        }
        return -1;
    }

    private static int isConsistent(int[] arr) {
        double res = Math.random() * 100 - 75;

        if (res > 0) {
            return (int) Math.round(res / 10);
        } else {
            return 0;
        }
    }
}