
ALTER TABLE dbo.Services
ADD is_active BIT NOT NULL CONSTRAINT DF_Services_is_active DEFAULT 1;
GO

ALTER TABLE dbo.Services
ADD updated_at DATETIME2(7) NULL;
GO

ALTER TABLE dbo.Products
ADD created_at DATETIME2(7) NOT NULL CONSTRAINT DF_Products_created_at DEFAULT GETDATE();
GO

ALTER TABLE dbo.Products
ADD updated_at DATETIME2(7) NULL;
GO

ALTER TABLE dbo.Orders
ADD payment_method VARCHAR(50) NULL,
    payment_status VARCHAR(50) NULL;
GO

UPDATE dbo.Services
SET is_active = 1
WHERE is_active IS NULL;
GO

IF COL_LENGTH('Services', 'category') IS NULL
BEGIN
    ALTER TABLE Services
    ADD category NVARCHAR(50) NOT NULL
        CONSTRAINT DF_Services_Category DEFAULT 'General';
END
GO

UPDATE Services
SET category =
    CASE
        WHEN name LIKE N'%Groom%' THEN N'Grooming'
        WHEN name LIKE N'%Vaccination%' THEN N'Veterinary'
        WHEN name LIKE N'%Socialization%' THEN N'Training'
        ELSE N'General'
    END
WHERE category = 'General';
GO