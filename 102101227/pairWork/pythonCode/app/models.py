from django.db import models
from rest_framework.renderers import  JSONRenderer
# Create your models here.

class Param(models.Model):
    data0 = models.IntegerField(verbose_name='数据0',default=0)
    data1 = models.IntegerField(verbose_name='数据1',default=0)
    data2 = models.IntegerField(verbose_name='数据2',default=0)
    data3 = models.IntegerField(verbose_name='数据3',default=0)
    data4 = models.IntegerField(verbose_name='数据4',default=0)
    score = models.IntegerField(verbose_name='积分',default=0)

    def __str__(self):
        return str(self.score)

    class Meta:
        db_table = 'param'
        verbose_name = '参数'

