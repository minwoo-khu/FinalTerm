from django.contrib import admin
from .models import WasteRecord

@admin.register(WasteRecord)
class WasteRecordAdmin(admin.ModelAdmin):
    list_display = ('id', 'waste_type', 'confidence', 'created_at')
