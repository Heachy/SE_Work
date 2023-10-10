from django.urls import path,re_path
from . import views

urlpatterns = [
    path('data/',views.score_get),
    path('data/<int:id>/',views.score_detail),#普通传参，注意要排序
    path('datacreate/',views.score_create),
    path('tablecreate/',views.table_create),
    path('aidata/<int:data1>/<int:data2>/<int:data3>/<int:data4>/',views.AI_url),#ai对战传参/11/112  注意要排序
    #re_path('users/(.+?)/',views.user_detail)
]
#pip freeze >> requirements.txt
