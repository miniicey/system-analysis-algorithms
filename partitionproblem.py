def greedy_partition(arr):
    arr = sorted(arr, reverse=True)

    subset1 = []
    subset2 = []
    sum1= 0
    sum2 = 0

    for num in arr:
        if sum1 <= sum2:
            subset1.append(num)
            sum1 += num
        else:
            subset2.append(num)
            sum2+=num

    return subset1, subset2, abs(sum1 -sum2)
        

arr = [0, 20, 15, 5 ,2]  
s1, s2, diff = greedy_partition(arr)

print("subset", s1)
print("subset2", s2)
print("difference", diff)




