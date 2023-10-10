"""

定义DRF框架的序列化器类


"""
from .models import Param
from rest_framework import serializers
class ParamSerializer(serializers.ModelSerializer):
    """id = serializers.IntegerField(read_only=True)
    score = serializers.IntegerField(read_only=True)
    data0 = serializers.IntegerField()
    data1 = serializers.IntegerField()
    data2 = serializers.IntegerField()
    data3 = serializers.IntegerField()
    data4 = serializers.IntegerField()
    def create(self, validated_data):
        return Param.objects.create(validated_data)"""
    """用户信息序列化器"""
    class Meta:
        model = Param    #指明参照那个模型类
        fields = '__all__'     #指明为模型类的那些字段生成
        #指定字段 fields = ('id','name','email')
        #排除某些字段不参与序列化exclude =('id',)
        #指明只读字段read_only_field
        #extra_kwargs = {'mobile':'max_length':11,'min_length':18,'requires':True}  重新定义反序列化规则
