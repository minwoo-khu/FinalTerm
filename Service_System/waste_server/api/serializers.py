from rest_framework import serializers
from .models import WasteRecord

class WasteSerializer(serializers.ModelSerializer):
    class Meta:
        model = WasteRecord
        fields = '__all__'
