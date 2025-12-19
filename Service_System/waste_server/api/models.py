from django.db import models

class WasteRecord(models.Model):
    image = models.ImageField(upload_to='waste/')
    waste_type = models.CharField(max_length=20)
    confidence = models.FloatField()
    created_at = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return f"{self.waste_type} ({self.created_at})"
