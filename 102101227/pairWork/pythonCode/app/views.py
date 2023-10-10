from django.shortcuts import render
from django.http import JsonResponse
from .models import Param  # 导入模型
from .serializers import ParamSerializer  # 导入序列化器
import json


# Create your views here.
def score_get(request):
    if request.method == 'POST':
        params = json.loads(request.body.decode())
        post_id = int(params['score'])
        ser = ParamSerializer(data=params)
        if (ser.is_valid()):
            # 检验通过，添加数据到数据库
            obj = Param.objects.get(id=post_id)
            return JsonResponse({"code": 200, "score": obj.score, "message": "OK"}, status=200)
        else:
            return JsonResponse({"code": 400, "message": ser.errors}, status=400)
        """
        else:
            return JsonResponse({'code': 400, 'message': ser.errors}, status=400)"""


def score_detail(request, id):
    if request.method == 'GET':
        # id必须要递增的数字，如11345
        # 如31145就不行
        obj = Param.objects.get(id=id)
        return JsonResponse({"code": 200, "score": obj.score, "message": "OK"}, status=200)


def score_create(request):
    if request.method == 'POST':
        params = json.loads(request.body.decode())
        param_0 = int(params['data0'])
        param_1 = int(params['data1'])
        param_2 = int(params['data2'])
        param_3 = int(params['data3'])
        param_4 = int(params['data4'])
        param_id = param_0 * 10000 + param_1 * 1000 + param_2 * 100 + param_3 * 10 + param_4
        param_score = int(params['score'])
        Param.objects.create(id=param_id, data0=param_0, data1=param_1, data2=param_2, data3=param_3, data4=param_4,
                             score=param_score)
        return JsonResponse({"code": 200, "message": "OK"}, status=200)


def table_create(request):
    for param_0 in range(1, 7):
        for param_1 in range(param_0, 7):
            for param_2 in range(param_1, 7):
                for param_3 in range(param_2, 7):
                    for param_4 in range(param_3, 7):
                        counts = {}
                        counts[param_0] = counts.get(param_0, 0) + 1
                        counts[param_1] = counts.get(param_1, 0) + 1
                        counts[param_2] = counts.get(param_2, 0) + 1
                        counts[param_3] = counts.get(param_3, 0) + 1
                        counts[param_4] = counts.get(param_4, 0) + 1
                        # 创建一个字典，如果有重复就+1，没有就创建一个
                        # counts = sorted(counts.items(), key=lambda x: x[1], reverse=True)
                        # 按值升序排列
                        param_score = param_0 + param_1 + param_2 + param_3 + param_4
                        param_id = param_0 * 10000 + param_1 * 1000 + param_2 * 100 + param_3 * 10 + param_4
                        if wulian(counts):
                            Param.objects.create(id=param_id, data0=param_0, data1=param_1, data2=param_2,
                                                 data3=param_3, data4=param_4, score=param_score + 100)
                            continue
                        elif shunzi(counts):
                            Param.objects.create(id=param_id, data0=param_0, data1=param_1, data2=param_2,
                                                 data3=param_3, data4=param_4, score=param_score + 60)
                            continue
                        elif silian(counts):
                            Param.objects.create(id=param_id, data0=param_0, data1=param_1, data2=param_2,
                                                 data3=param_3, data4=param_4, score=param_score + 40)
                            continue
                        elif xiaoshunzi(counts):
                            Param.objects.create(id=param_id, data0=param_0, data1=param_1, data2=param_2,
                                                 data3=param_3, data4=param_4, score=param_score + 30)
                            continue
                        elif hulu(counts):
                            Param.objects.create(id=param_id, data0=param_0, data1=param_1, data2=param_2,
                                                 data3=param_3, data4=param_4, score=param_score + 20)
                            continue
                        elif san_and_shuang(counts):
                            Param.objects.create(id=param_id, data0=param_0, data1=param_1, data2=param_2,
                                                 data3=param_3, data4=param_4, score=param_score + 10)
                            continue
                        else:
                            Param.objects.create(id=param_id, data0=param_0, data1=param_1, data2=param_2,
                                                 data3=param_3, data4=param_4, score=param_score)

    return JsonResponse({"code": 200, "message": "Table create!"}, status=200)


def wulian(counts):
    if len(counts) == 1:
        return True
    return False


def shunzi(counts):
    if len(counts) == 5:
        if counts.get(1, 0) ^ counts.get(6, 0):
            return True
    return False


def silian(counts):
    for value in counts.values():
        if value == 4:
            return True
    return False


def xiaoshunzi(counts):
    if (counts.get(1, 0) > 0) & (counts.get(2, 0) > 0) & (counts.get(3, 0) > 0) & (counts.get(4, 0) > 0):
        return True
    elif (counts.get(2, 0) > 0) & (counts.get(3, 0) > 0) & (counts.get(4, 0) > 0) & (counts.get(5, 0) > 0):
        return True
    elif (counts.get(3, 0) > 0) & (counts.get(4, 0) > 0) & (counts.get(5, 0) > 0) & (counts.get(6, 0) > 0):
        return True
    return False


def hulu(counts):
    if len(counts) == 2:
        return True
    return False


def san_and_shuang(counts):
    if len(counts) == 3:
        return True
    return False


def AI_url(request, data1, data2, data3, data4):
    if request.method == 'GET':
        score = {}
        weight = {}
        table_create_score(score)
        weight_table(score, weight)
        weight[0] = 0
        ser1 = AI_select(data1, data2, weight)
        ser2 = AI_select(data3, data4, weight)
        rage = rage_get(ser1, ser2)
        if rage:
            is_rage = True
        else:
            is_rage = False
        return JsonResponse({"code": 200, "data": ser1['weight_id'], "is_rage": is_rage, "rage": rage, "message": "OK"},
                            status=200)


def rage_get(QZ1, QZ2):
    if QZ1['weight_score'] < QZ2['weight_score']:
        return 0
    elif silian(num_count(QZ1['weight_id'])) or xiaoshunzi(num_count(QZ1['weight_id'])):
        return 3
    elif QZ1['weight_score'] - QZ2['weight_score'] > 10:
        return 3
    elif QZ1['weight_score'] - QZ2['weight_score'] > 5:
        return 2
    else:
        return 1


def table_create_score(score):
    for param_0 in range(1, 7):
        for param_1 in range(param_0, 7):
            for param_2 in range(param_1, 7):
                for param_3 in range(param_2, 7):
                    for param_4 in range(param_3, 7):
                        counts = {}
                        counts[param_0] = counts.get(param_0, 0) + 1
                        counts[param_1] = counts.get(param_1, 0) + 1
                        counts[param_2] = counts.get(param_2, 0) + 1
                        counts[param_3] = counts.get(param_3, 0) + 1
                        counts[param_4] = counts.get(param_4, 0) + 1
                        # 创建一个字典，如果有重复就+1，没有就创建一个
                        # counts = sorted(counts.items(), key=lambda x: x[1], reverse=True)
                        # 按值升序排列
                        param_score = param_0 + param_1 + param_2 + param_3 + param_4
                        param_id = param_0 * 10000 + param_1 * 1000 + param_2 * 100 + param_3 * 10 + param_4
                        if wulian(counts):
                            score[param_id] = param_score + 100
                            continue
                        if shunzi(counts):
                            score[param_id] = param_score + 60
                            continue
                        if silian(counts):
                            score[param_id] = param_score + 40
                            continue
                        if xiaoshunzi(counts):
                            score[param_id] = param_score + 30
                            continue
                        if hulu(counts):
                            score[param_id] = param_score + 20
                            continue
                        if san_and_shuang(counts):
                            score[param_id] = param_score + 10
                            continue
                        score[param_id] = param_score


def AI_select(num1, num2, weight):
    len_1 = 0
    # num1:已经选好的数字，len_1数字位数
    len_2 = 0
    # num2:还未选好的数字，len_2数字位数
    s1 = num1
    s2 = num2
    count = {}
    list = []
    while s1 > 0 or s2 > 0:
        if s1 > 0:
            num = s1 % 10
            len_1 += 1
            s1 = s1 // 10
            count[num] = count.get(num, 0) + 1
        if s2 > 0:
            len_2 += 1
            s2 = s2 // 10
    s1 = num1
    s2 = num2
    QZ = {'weight_id': num1, 'weight_score': weight[num1]}
    param_0 = s2 % 10
    param_1 = int(s2 / 10) % 10
    param_2 = int(s2 / 100) % 10
    param_3 = int(s2 / 1000) % 10
    param_4 = int(s2 / 10000) % 10
    list.append(param_0)
    list.append(param_1)
    list.append(param_2)
    list.append(param_3)
    list.append(param_4)
    for a in range(len_2):
        count[list[a]] = count.get(list[a], 0) + 1
        if weight[weight_id_get(count)] > QZ['weight_score']:
            QZ['weight_id'] = weight_id_get(count)
            QZ['weight_score'] = weight[weight_id_get(count)]
        for b in range(a + 1, len_2):
            count[list[b]] = count.get(list[b], 0) + 1
            if weight[weight_id_get(count)] > QZ['weight_score']:
                QZ['weight_id'] = weight_id_get(count)
                QZ['weight_score'] = weight[weight_id_get(count)]
            for c in range(b + 1, len_2):
                count[list[c]] = count.get(list[c], 0) + 1
                if weight[weight_id_get(count)] > QZ['weight_score']:
                    QZ['weight_id'] = weight_id_get(count)
                    QZ['weight_score'] = weight[weight_id_get(count)]
                for d in range(c + 1, len_2):
                    count[list[d]] = count.get(list[d], 0) + 1
                    if weight[weight_id_get(count)] > QZ['weight_score']:
                        QZ['weight_id'] = weight_id_get(count)
                        QZ['weight_score'] = weight[weight_id_get(count)]
                    for e in range(d + 1, len_2):
                        count[list[e]] = count.get(list[e], 0) + 1
                        if weight[weight_id_get(count)] > QZ['weight_score']:
                            QZ['weight_id'] = weight_id_get(count)
                            QZ['weight_score'] = weight[weight_id_get(count)]
                        count[list[e]] = count.get(list[e], 0) - 1
                    count[list[d]] = count.get(list[d], 0) - 1
                count[list[c]] = count.get(list[c], 0) - 1
            count[list[b]] = count.get(list[b], 0) - 1
        count[list[a]] = count.get(list[a], 0) - 1

    """for len_3 in range(len_2):
        s2 = num2
        num = s2 % 10
        s2 = s2 // 10
        s1 = s1 * 10 + num
        print(s1)
        if weight[weight_id_get(num_count(s1))] > QZ['weight_score']:
            QZ['weight_id'] = weight_id_get(num_count(s1))
            QZ['weight_score'] = weight[weight_id_get(num_count(s1))]
        len0 = len_2 - len_3 - 1
        while len0 > 0:
            s1 = s1 // 10
            num = s2 % 10
            s2 = s2 // 10
            s1 = s1 * 10 + num
            len0 -= 1
            print(s1)
            if weight[weight_id_get(num_count(s1))] > QZ['weight_score']:
                QZ['weight_id'] = weight_id_get(num_count(s1))
                QZ['weight_score'] = weight[weight_id_get(num_count(s1))]"""
    """缺陷代码，考虑不够完整"""
    return QZ


def num_count(num):
    count = {}
    while num > 0:
        num_0 = num % 10
        count[num_0] = count.get(num_0, 0) + 1
        num = num // 10
        count = dict(sorted(count.items(), key=lambda x: x[0], reverse=False))
    return count


def weight_table(score, weight):
    for param_0 in range(1, 7):
        for param_1 in range(param_0, 7):
            for param_2 in range(param_1, 7):
                for param_3 in range(param_2, 7):
                    for param_4 in range(param_3, 7):
                        counts = {}
                        counts[param_0] = counts.get(param_0, 0) + 1
                        weight_get(counts, score, weight)
                        counts[param_1] = counts.get(param_1, 0) + 1
                        weight_get(counts, score, weight)
                        counts[param_2] = counts.get(param_2, 0) + 1
                        weight_get(counts, score, weight)
                        counts[param_3] = counts.get(param_3, 0) + 1
                        weight_get(counts, score, weight)
                        counts[param_4] = counts.get(param_4, 0) + 1
                        weight_get(counts, score, weight)


def weight_get(count, score, weight):
    # counts 储存数字
    # 传入的是字典
    n = 0  # 姑且称为权数
    s = 0  # 姑且成为重数
    for param_0 in range(1, 7):
        for param_1 in range(param_0, 7):
            for param_2 in range(param_1, 7):
                for param_3 in range(param_2, 7):
                    for param_4 in range(param_3, 7):
                        flag = True
                        counts = {}
                        counts[param_0] = counts.get(param_0, 0) + 1
                        counts[param_1] = counts.get(param_1, 0) + 1
                        counts[param_2] = counts.get(param_2, 0) + 1
                        counts[param_3] = counts.get(param_3, 0) + 1
                        counts[param_4] = counts.get(param_4, 0) + 1
                        param_id = param_0 * 10000 + param_1 * 1000 + param_2 * 100 + param_3 * 10 + param_4
                        for key in count.keys():
                            if counts.get(key, 0) < count[key]:
                                flag = False
                        if flag:
                            n += 1
                            s += score[param_id]
    weight[weight_id_get(count)] = int(s / n)


def weight_id_get(count):
    count = dict(sorted(count.items(), key=lambda x: x[0], reverse=False))
    weight_id = 0
    rage = 10
    for key, value in count.items():
        weight_value = value
        while weight_value > 0:
            weight_id *= rage
            weight_id += key
            weight_value -= 1
    return weight_id
