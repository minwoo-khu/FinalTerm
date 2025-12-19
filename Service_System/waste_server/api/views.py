from rest_framework import viewsets
from .models import WasteRecord
from .serializers import WasteSerializer

class WasteViewSet(viewsets.ModelViewSet):
    queryset = WasteRecord.objects.all().order_by('-created_at')
    serializer_class = WasteSerializer
