# Generated by Django 4.2.5 on 2023-10-01 09:27

from django.db import migrations, models


class Migration(migrations.Migration):

    initial = True

    dependencies = [
    ]

    operations = [
        migrations.CreateModel(
            name='Param',
            fields=[
                ('id', models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('data0', models.IntegerField(default=0, verbose_name='数据0')),
                ('data1', models.IntegerField(default=0, verbose_name='数据1')),
                ('data2', models.IntegerField(default=0, verbose_name='数据2')),
                ('data3', models.IntegerField(default=0, verbose_name='数据3')),
                ('data4', models.IntegerField(default=0, verbose_name='数据4')),
                ('score', models.IntegerField(default=0, verbose_name='积分')),
            ],
            options={
                'verbose_name': '参数',
                'db_table': 'param',
            },
        ),
    ]
